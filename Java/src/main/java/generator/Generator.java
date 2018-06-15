package main.java.generator;

import main.java.dataset.intervals.CallIntervals;
import main.java.dataset.intervals.CallerTypesClustering;
import main.java.dataset.model.AbstractCallDuration;
import main.java.dataset.model.CallDuration;
import main.java.dataset.util.AbstractReader;
import main.java.dataset.model.CallRecord;
import main.java.dataset.util.Tuple;
import main.java.social_network.SocialNetworkExtractor;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Generator extends AbstractReader {

    public static final String OUTPUT_FILE = "./../cdr_output/%d.csv";
    public static final String EXPECTED_CALLS_FILE = "./../cdr_output/%d-expectedCalls.csv";
    public static final String CALLERS_LIST = "./../dataset/dataCluster"+35+".csv";
    public static final String PROFILE_TYPES = "./../dataset/centroids"+35+".csv";
    public static final String SOCIAL_NETWORK = SocialNetworkExtractor.OUTPUT_PATH;
    public static final String SEP = CallRecord.SEP;

    private int[] userProfiles;
    private Random random;
    private double[][] profileFeatures;
    private Map<Integer, Map<Integer, Double>> userConnections;
    private Map<Integer, AbstractCallDuration> groupDuration;
    private double max = Double.MIN_VALUE;
    private int userCount;

    public Generator() {
        CallIntervals.initialize();
        random = new Random();
    }

    public void init(int userCount) throws IOException {
        this.userCount = userCount;

        userProfiles = new int[userCount];
        readInputAndDoStuff(CALLERS_LIST, line -> {
            String[] parts = line.split(SEP);
            userProfiles[Integer.parseInt(parts[0])] = Integer.parseInt(parts[1]);
        });

        profileFeatures = new double[35][70];//TODO unhardcode
        readInputAndDoStuff(PROFILE_TYPES, line -> {
            String[] parts = line.split(SEP);
            int cluster = Integer.parseInt(parts[0]);
            int feature = Integer.parseInt(parts[1]);
            profileFeatures[cluster][feature] = Double.parseDouble(parts[2]);
            max = Double.max(profileFeatures[cluster][feature], max);
        });
//        for (int cluster = 0; cluster < profileFeatures.length; cluster++) {
//            System.out.println(Arrays.toString(profileFeatures[cluster]));
//        }
//        System.out.println(max);

        groupDuration = new HashMap<>();
        //TODO implement this to read from file in format group;type;other_parameters
        for (int i = 0; i < profileFeatures.length; i++) {
            groupDuration.put(i, CallDuration.parseFromString("1;dffd", SEP));
        }

        userConnections = new HashMap<>();
        readInputAndDoStuffNoSkip(SOCIAL_NETWORK, line -> {
            String[] parts = line.split(SEP);
            int caller = Integer.parseInt(parts[0]);
            int receiver = Integer.parseInt(parts[1]);
            double connection = Double.parseDouble(parts[2]);
            Map<Integer, Double> receiverConnection = userConnections.getOrDefault(caller, new HashMap<>());
            receiverConnection.put(receiver, connection);
            userConnections.put(caller, receiverConnection);
        });
    }

    public void run(int day) throws IOException {
        //TODO run it for particular day, not just Monday
        long currentTime = System.currentTimeMillis();

        String outputFile = String.format(OUTPUT_FILE, currentTime);
        String expectedCallsFile = String.format(EXPECTED_CALLS_FILE, currentTime);

        BufferedWriter outputWriter = Files.newBufferedWriter(Paths.get(outputFile));
        BufferedWriter expectedCallsWriter = Files.newBufferedWriter(Paths.get(expectedCallsFile));

        double[][] userIntervalCallCount = new double[userProfiles.length][10];
        writeln(expectedCallsWriter, "user" + SEP + "interval" + SEP + "cnt");
        for (int user = 0; user < userIntervalCallCount.length; user++) {
            for (int interval = 0; interval < 10; interval++) {
                double expectedCallCount = profileFeatures[userProfiles[user]][interval];
                userIntervalCallCount[user][interval] = Math.max(Math.round(random.nextGaussian() * expectedCallCount + expectedCallCount), 0);
                writeln(expectedCallsWriter, user + SEP + interval + SEP + userIntervalCallCount[user][interval]);
            }
        }
        flushAndClose(expectedCallsWriter);

        Map<Integer, Integer> busyNodes = new HashMap<>();

        int interval = 0;
        for (Map.Entry<Tuple<Double>,String> tupleStringEntry : CallIntervals.timeIntervalMap.entrySet()) {

            Set<Integer> willMakeCall = new HashSet<>();
            for (int user = 0; user < userIntervalCallCount.length; user++) {
                if (userIntervalCallCount[user][interval] > 0) {
                    willMakeCall.add(user);
                }
            }

            double startDecimalTime = tupleStringEntry.getKey().getV1();
            double endDecimalTime = tupleStringEntry.getKey().getV2();

            for (int hour = (int)startDecimalTime; hour < (int)endDecimalTime + 1; hour++) {

                int startMinute = minutesForInterval(startDecimalTime, hour, true);
                int endMinute = minutesForInterval(endDecimalTime, hour, false);
                for (int minute = startMinute; minute < endMinute; minute++) {

                    for (int second = 0; second < 60; second++) {

                        //if the call stopped this second, free the user
                        Set<Integer> freed = new HashSet<>();
                        for (Map.Entry<Integer, Integer> busyRemaining : busyNodes.entrySet()) {
                            if (busyRemaining.getValue() > 1) {
                                busyRemaining.setValue(busyRemaining.getValue() - 1);
                            }
                            else {
                                freed.add(busyRemaining.getKey());
                            }
                        }
                        for (Integer freedUser : freed) {
                            busyNodes.remove(freedUser);
                        }

                        Set<Integer> wontMakeCallsAnyMore = new HashSet<>();
                        for (int user : willMakeCall) {
                            if (!busyNodes.containsKey(user)) {
                                double timeSize = (endDecimalTime - startDecimalTime) * 60 * 60;
                                if (Math.random() < 1 / timeSize) {
                                    userIntervalCallCount[user][interval]--;
                                    if (userIntervalCallCount[user][interval] == 0) {
                                        wontMakeCallsAnyMore.add(user);
                                    }
                                    int duration = groupDuration.get(userProfiles[user]).getCallDuration();
                                    busyNodes.put(user, duration);
                                    
                                    double callee = Math.random();
                                    double total = 0;
                                    //TODO check, there was an NullPointerException here once and there shouldn't have been
                                    for (Map.Entry<Integer,Double> userConnectedness : userConnections.get(user).entrySet()) {
                                        total += userConnectedness.getValue();
                                        if (callee >= total) {
                                            //check if the called user is busy
                                            if (busyNodes.containsKey(userConnectedness.getKey())) {
                                                duration = -1;
                                                busyNodes.put(user, 1);
                                            } else {
                                                busyNodes.put(userConnectedness.getKey(), duration);
                                            }
                                            printLine(outputWriter, user + SEP + userConnectedness.getKey() + SEP + duration + SEP + hour + ":" + minute + ":"+second);
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                        willMakeCall.removeAll(wontMakeCallsAnyMore);
                    }
                }
            }
            interval++;
        }
        
        flushAndClose(outputWriter);
    }

    public static int minutesFromDecimal(double decimalTime) {
        int hours = (int)decimalTime;
        return (int)((decimalTime - hours)*60);
    }

    public static int minutesForInterval(double decimalIntervalBorder, int currentHour, boolean start) {
        int intervalBorderHours = (int)(decimalIntervalBorder);
        if (currentHour == intervalBorderHours) {
            return minutesFromDecimal(decimalIntervalBorder);
        }
        if (start) {
            return 0;
        } else {
            return 60;
        }
    }

    //TODO remove this, it's completely useless other than it's fast to switch between writeln and printLine
    private void printLine(BufferedWriter writer, String string) throws IOException {
        System.out.println(string);
        writeln(writer, string);
    }

    public static void main(String[] args) throws IOException {
        Generator generator = new Generator();
        generator.init(CallerTypesClustering.USER_COUNT);
        generator.run(2);
    }

}
