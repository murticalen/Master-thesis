package main.java.generator;

import main.java.dataset.intervals.CallIntervals;
import main.java.dataset.intervals.CallerTypesClustering;
import main.java.dataset.model.AbstractCallDuration;
import main.java.dataset.model.CallDuration;
import main.java.dataset.util.AbstractReader;
import main.java.dataset.util.CallRecord;
import main.java.dataset.util.Tuple;
import main.java.social_network.SocialNetworkExtractor;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Generator extends AbstractReader {

    public static final String OUTPUT_FILE = "./../cdr_output/%d.csv";
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
        readInputAndDoStuffNoSkip(CALLERS_LIST, line -> {
            String[] parts = line.split(SEP);
            userProfiles[Integer.parseInt(parts[0])] = Integer.parseInt(parts[1]);
        });

        profileFeatures = new double[35][];//TODO unhardcode
        readInputAndDoStuffNoSkip(PROFILE_TYPES, line -> {
            String[] parts = line.split(SEP);
            int cluster = Integer.parseInt(parts[0]);
            profileFeatures[cluster] = new double[10];//TODO unhardcode
            for (int i = 1; i < parts.length; i++) {
                profileFeatures[cluster][i-1] = Double.parseDouble(parts[i]);
                max = Double.max(profileFeatures[cluster][i-1], max);
            }
        });

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
        String outputFile = String.format(OUTPUT_FILE, System.currentTimeMillis());
        BufferedWriter writer = Files.newBufferedWriter(Paths.get(outputFile));
        
        double[][] userIntervalCallCount = new double[userProfiles.length][10];
        for (int user = 0; user < userIntervalCallCount.length; user++) {
            for (int interval = 0; interval < profileFeatures[1].length; interval++) {
                double expectedCallCount = profileFeatures[userProfiles[user]][interval];
                userIntervalCallCount[user][interval] = Math.max(Math.round(random.nextGaussian() * expectedCallCount + expectedCallCount), 0);
            }
        }

        Map<Integer, Integer> busyNodes = new HashMap<>();

        int interval = 0;
        for (Map.Entry<Tuple<Double>,String> tupleStringEntry : CallIntervals.timeIntervalMap.entrySet()) {
            double start = tupleStringEntry.getKey().getV1();
            double end = tupleStringEntry.getKey().getV2();
            for (int hour = (int)start; hour < (int)end + 1; hour++) {
                for (int minute = 0; minute < 60; minute++) {
                    for (int second = 0; second < 60; second++) {
                        for (int user = 0; user < userCount; user++) {
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
                            if (userIntervalCallCount[user][interval] > 0 && !busyNodes.containsKey(user)) {
                                double timeSize = (end - start) * 60 * 60;
                                if (Math.random() < 1 / timeSize) {
                                    userIntervalCallCount[user][interval]--;
                                    int duration = groupDuration.get(userProfiles[user]).getCallDuration();
                                    busyNodes.put(user, duration);
                                    
                                    double callee = Math.random();
                                    double total = 0;
                                    for (Map.Entry<Integer,Double> userConnectedness : userConnections.get(user).entrySet()) {
                                        total += userConnectedness.getValue();
                                        if (callee >= total) {
                                            busyNodes.put(userConnectedness.getKey(), duration);
                                            writeln(writer, user + SEP + userConnectedness.getKey() + SEP + duration + SEP + hour + ":" + minute + ":"+second);
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            interval++;
        }
        
        flushAndClose(writer);
    }

    private static void writeCallEvent(BufferedWriter writer) throws IOException
    {
        writeln(writer, "");
    }

    public static void main(String[] args) throws IOException {
        Generator generator = new Generator();
        generator.init(CallerTypesClustering.USER_COUNT);
        generator.run(2);
    }

}
