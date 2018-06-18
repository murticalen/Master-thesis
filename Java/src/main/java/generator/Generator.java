package main.java.generator;

import main.java.dataset.DatasetMain;
import main.java.dataset.intervals.CallIntervals;
import main.java.dataset.intervals.CallerTypesClustering;
import main.java.dataset.model.AbstractCallDuration;
import main.java.dataset.model.CallDuration;
import main.java.dataset.model.CallRecord;
import main.java.dataset.util.AbstractReader;
import main.java.dataset.model.Tuple;
import main.java.dataset.util.Constants;
import main.java.generator.determine_call_count.AbstractCallCountDeterminator;
import main.java.generator.determine_call_count.ClusteredCallCountDeterminator;
import main.java.generator.determine_call_count.PersonalCallCountDeterminator;
import main.java.social_network.SocialNetworkExtractor;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Generator extends AbstractReader {

    public static final String OUTPUT_FILE = "./../cdr_output/%d.csv";
    public static final String EXPECTED_CALLS_FILE = "./../cdr_output/%d-expectedCalls.csv";
    public static final String CALLERS_LIST = "./../dataset/dataCluster" + 35 + ".csv";
    public static final String PROFILE_TYPES = "./../dataset/centroids" + 35 + ".csv";
    public static final String FRAUDS = "./../dataset/frauds.csv";
    public static final String SOCIAL_NETWORK = SocialNetworkExtractor.OUTPUT_PATH;
    public static final String SEP = Constants.SEPARATOR;

    private static Random random = new Random();
    private AbstractCallCountDeterminator callCountDeterminator;
    private double[][] fraudFeaures;
    private Map<Integer, Map<Integer, Double>> userConnections;
    private Map<Integer, AbstractCallDuration> groupDuration;
    private int userCount;
    private static long generatedCalls;

    public Generator(AbstractCallCountDeterminator callCountDeterminator) {
        CallIntervals.initialize();
        this.callCountDeterminator = callCountDeterminator;
    }

    public void init(int userCount) throws IOException {
        this.userCount = userCount;
        callCountDeterminator.readData();

        fraudFeaures = new double[35][70];//TODO unhardcode
        readInputAndDoStuff(FRAUDS, line -> {
            String[] parts = line.split(SEP);
            int cluster = Integer.parseInt(parts[0]);
            int feature = Integer.parseInt(parts[1]);
            fraudFeaures[cluster][feature] = Double.parseDouble(parts[2]);
        });

        groupDuration = new HashMap<>();
        //TODO implement this to read from file in format group;type;other_parameters
        for (int i = 0; i < 35; i++) {
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
        generatedCalls = 0;

        //TODO run it for particular day, not just Monday
        long currentTime = System.currentTimeMillis();

        String outputFile = String.format(OUTPUT_FILE, currentTime);
        String expectedCallsFile = String.format(EXPECTED_CALLS_FILE, currentTime);

        BufferedWriter outputWriter = Files.newBufferedWriter(Paths.get(outputFile));

        long[][] userIntervalCallCount = callCountDeterminator.determineUserCallCount(day - 1, expectedCallsFile);
        System.out.println("Real-life " + Math.round(callCountDeterminator.getTotalExpectedCalls()) + " calls");
        System.out.println("Expected " + callCountDeterminator.getGeneratedExpectedCalls() + " calls");

        Map<Integer, Integer> busyNodes = new HashMap<>();

        int interval = 0;
        long problem = 0;
        for (Tuple<Double> timeInterval : CallIntervals.timeIntervalMap.keySet()) {

            Set<Integer> willMakeCall = new HashSet<>();
            for (int user = 0; user < userIntervalCallCount.length; user++) {
                if (userIntervalCallCount[user][interval] > 0) {
                    willMakeCall.add(user);
                }
            }

            double startDecimalTime = timeInterval.getV1();
            double endDecimalTime = timeInterval.getV2();

            for (int hour = (int) startDecimalTime; hour < (int) endDecimalTime + 1; hour++) {

                int startMinute = GeneratorHelper.intervalMinutesForCurrentHour(startDecimalTime, hour, true);
                int endMinute = GeneratorHelper.intervalMinutesForCurrentHour(endDecimalTime, hour, false);
                for (int minute = startMinute; minute < endMinute; minute++) {

                    for (int second = 0; second < 60; second++) {

                        //if the call stopped this second, free the user
                        Set<Integer> freed = new HashSet<>();
                        for (Map.Entry<Integer, Integer> busyRemaining : busyNodes.entrySet()) {
                            if (busyRemaining.getValue() > 1) {
                                busyRemaining.setValue(busyRemaining.getValue() - 1);
                            } else {
                                freed.add(busyRemaining.getKey());
                            }
                        }
                        for (Integer freedUser : freed) {
                            busyNodes.remove(freedUser);
                        }

                        Set<Integer> wontMakeCallsAnyMore = new HashSet<>();
                        for (int user : willMakeCall) {
                            if (!busyNodes.containsKey(user)) {
                                double timeSize = (endDecimalTime - startDecimalTime) * 3600 / userIntervalCallCount[user][interval];
                                if (Math.random() < 1.0 / timeSize) {
                                    userIntervalCallCount[user][interval]--;
                                    if (userIntervalCallCount[user][interval] == 0) {
                                        wontMakeCallsAnyMore.add(user);
                                    }
                                    int duration = groupDuration.get(0).getCallDuration();
                                    busyNodes.put(user, duration);

                                    double callee = Math.random();
                                    double total = 0;
                                    boolean called = false;
                                    for (Map.Entry<Integer, Double> userConnectedness : userConnections.get(user).entrySet()) {
                                        total += userConnectedness.getValue();
                                        if (total >= callee) {
                                            //check if the called user is busy
                                            if (busyNodes.containsKey(userConnectedness.getKey())) {
                                                duration = -1;
                                                busyNodes.put(user, 1);
                                            } else {
                                                busyNodes.put(userConnectedness.getKey(), duration);
                                            }
                                            printLine(outputWriter, user + SEP + userConnectedness.getKey() + SEP + duration + SEP + hour + ":" + minute + ":" + second);
                                            generatedCalls++;
                                            called = true;
                                            break;
                                        }
                                    }
                                    if (!called) {
                                        problem++;
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
        System.out.println("Real-life " + Math.round(callCountDeterminator.getTotalExpectedCalls()) + " calls");
        System.out.println("Expected " + callCountDeterminator.getGeneratedExpectedCalls() + " calls");
        System.out.println("Generated " + generatedCalls + " calls");
        System.out.println("Polled without callee: " + problem);
    }

    //TODO remove this, it's completely useless other than it's fast to switch between writeln and printLine
    private void printLine(BufferedWriter writer, String string) throws IOException {
        System.out.println(string);
        writeln(writer, string);
    }

    public static void main(String[] args) throws IOException {

        Constants.readFromFile(null);

        AbstractCallCountDeterminator clusteredCallCountDeterminator = new ClusteredCallCountDeterminator
                (CALLERS_LIST, PROFILE_TYPES, Constants.USER_COUNT, 35);
        AbstractCallCountDeterminator personalCallCountDeterminator = new PersonalCallCountDeterminator
                (Constants.USER_COUNT, DatasetMain.USER_PROFILE_FILE);
        Generator generator = new Generator(personalCallCountDeterminator);
        generator.init(Constants.USER_COUNT);
        generator.run(2);
    }

}
