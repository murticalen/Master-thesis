package main.java.generator;

import main.java.configuration.ConfigReader;
import main.java.configuration.Constants;
import main.java.dataset.DatasetMain;
import main.java.dataset.intervals.CallIntervals;
import main.java.dataset.intervals.IntervalHelper;
import main.java.dataset.model.Tuple;
import main.java.dataset.util.AbstractReader;
import main.java.generator.duration.AbstractCallDuration;
import main.java.generator.duration.QuantileDuration;
import main.java.generator.user_features.AbstractUserFeatures;
import main.java.generator.user_features.ClusteredUserFeatures;
import main.java.generator.user_features.PersonalUserFeatures;
import main.java.social_network.SocialNetworkExtractor;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Generator extends AbstractReader {
    
    public static final String OUTPUT_FILE         = "./../cdr_output/%d.csv";
    public static final String EXPECTED_CALLS_FILE = "./../cdr_output/%d-expectedCalls.csv";
    public static final String CALLERS_LIST        = "./../dataset/dataCluster" + 35 + ".csv";
    public static final String PROFILE_TYPES       = "./../dataset/centroids" + 35 + ".csv";
    public static final String FRAUDS              = "./../dataset/frauds.csv";
    public static final String SOCIAL_NETWORK      = SocialNetworkExtractor.OUTPUT_PATH;
    public static final String SEP                 = Constants.SEPARATOR;
    
    private static Random                             random = new Random();
    private        AbstractUserFeatures               callCountDeterminator;
    private        double[][]                         fraudFeaures;
    private        Map<Integer, Map<Integer, Double>> userConnections;
    private        Map<Integer, AbstractCallDuration> userDurations;
    private        int                                userCount;
    private static long                               generatedCalls;
    private        int                                year;
    private        int                                month;
    private        int                                day;
    
    private double totalTime = 0;
    private long   nCalls    = 0;
    
    public Generator(AbstractUserFeatures callCountDeterminator) {
        CallIntervals.initialize();
        this.callCountDeterminator = callCountDeterminator;
    }
    
    public void init(int userCount) throws IOException {
        this.userCount = userCount;
        callCountDeterminator.readData();
        
        fraudFeaures = new double[35][70];//TODO unhardcode
        readInputAndDoStuff(FRAUDS, line -> {
            String[] parts   = line.split(SEP);
            int      cluster = Integer.parseInt(parts[0]);
            int      feature = Integer.parseInt(parts[1]);
            fraudFeaures[cluster][feature] = Double.parseDouble(parts[2]);
        });
        
        userDurations = new HashMap<>();
        readInputAndDoStuff("./../dataset/duration.csv", line -> AbstractCallDuration.parseFromString(userDurations, line, Constants.SEPARATOR));
        
        userConnections = new HashMap<>();
        readInputAndDoStuffNoSkip(SOCIAL_NETWORK, line -> {
            String[]             parts              = line.split(SEP);
            int                  caller             = Integer.parseInt(parts[0]);
            int                  receiver           = Integer.parseInt(parts[1]);
            double               connection         = Double.parseDouble(parts[2]);
            Map<Integer, Double> receiverConnection = userConnections.getOrDefault(caller, new HashMap<>());
            receiverConnection.put(receiver, connection);
            userConnections.put(caller, receiverConnection);
        });
    }
    
    public void run(int year, int month, int day, int weekDay) throws IOException {
        this.year = year < 2000 ? year : year % 2000;
        this.month = month;
        this.day = day;
        AbstractUserFeatures.clearExpectedCalls();
        generatedCalls = 0;
        long durationTotal = 0;
        long actualCalls   = 0;
        
        long currentTime = System.currentTimeMillis();
        
        String outputFile        = String.format(OUTPUT_FILE, currentTime);
        String expectedCallsFile = String.format(EXPECTED_CALLS_FILE, currentTime);
        
        BufferedWriter outputWriter = Files.newBufferedWriter(Paths.get(outputFile));
        
        long[][] userIntervalCallCount = callCountDeterminator.determineUserCallCount(weekDay - 1, expectedCallsFile);
        System.out.println("Real-life " + Math.round(callCountDeterminator.getTotalExpectedCalls()) + " calls");
        System.out.println("Expected " + callCountDeterminator.getGeneratedExpectedCalls() + " calls");
        
        BufferedReader inputReader = new BufferedReader(new InputStreamReader(System.in));
        inputReader.readLine();
        
        Map<Integer, Integer> busyNodes = new HashMap<>();
        
        int  interval = 0;
        long problem  = 0;
        for (Tuple<Double> timeInterval : CallIntervals.timeIntervalMap.keySet()) {
            
            totalTime = 0;
            nCalls = 0;
            
            Map<Integer, Long>    willMakeCallInInterval = new HashMap<>();
            Map<Integer, Integer> activatedUsers         = new HashMap<>();
            Map<Integer, Integer> userBusyCount          = new HashMap<>();
            //Map<Integer, List<Integer>>
            for (int user = 0; user < userIntervalCallCount.length; user++) {
                if (userIntervalCallCount[user][interval] > 0) {
                    willMakeCallInInterval.put(user, userIntervalCallCount[user][interval]);
                }
            }
            
            double startDecimalTime  = timeInterval.getV1();
            double endDecimalTime    = timeInterval.getV2();
            int    timeSize          = (int)((endDecimalTime - startDecimalTime) * 3600);
            int    remainingTimeSize = timeSize;
            
            for (int hour = (int)startDecimalTime; hour < (int)endDecimalTime + 1; hour++) {
                
                int startMinute = GeneratorHelper.intervalMinutesForCurrentHour(startDecimalTime, hour, true);
                int endMinute   = GeneratorHelper.intervalMinutesForCurrentHour(endDecimalTime, hour, false);
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
                        for (Map.Entry<Integer, Long> userTotalCalls : willMakeCallInInterval.entrySet()) {
                            int  user                     = userTotalCalls.getKey();
                            long totalUserCallsInInterval = userTotalCalls.getValue();
//                            if (GeneratorHelper.activateUser(timeSize, remainingTimeSize, totalUserCallsInInterval, userIntervalCallCount[user][interval])) {
//                                activatedUsers.put(user, activatedUsers.getOrDefault(user, 0) + 1);
//                            }
                            if (!busyNodes.containsKey(user)) {/*&& activatedUsers.containsKey(user)) {
                                int userActivatedCalls = activatedUsers.get(user);
                                if (userActivatedCalls > 0) {
                                    if (userActivatedCalls == 1) {
                                        activatedUsers.remove(user);
                                    } else {
                                        activatedUsers.put(user, userActivatedCalls - 1);
                                    }*/
                                if (GeneratorHelper.activateUser(timeSize, remainingTimeSize, totalUserCallsInInterval, userIntervalCallCount[user][interval], userBusyCount.getOrDefault(user, 0))) {
                                    userIntervalCallCount[user][interval]--;
                                    if (userIntervalCallCount[user][interval] == 0) {
                                        wontMakeCallsAnyMore.add(user);
                                    }
                                    int duration = userDurations.get(user).getCallDuration();
                                    busyNodes.put(user, duration);
                                    
                                    double  calleeRandom = Math.random();
                                    double  total        = 0;
                                    boolean called       = false;
                                    for (Map.Entry<Integer, Double> userConnectedness : userConnections.get(user).entrySet()) {
                                        total += userConnectedness.getValue();
                                        if (total >= calleeRandom) {
                                            int callee = userConnectedness.getKey();
                                            //check if the called user is busy
                                            if (busyNodes.containsKey(callee)) {
                                                duration = -1;
                                                busyNodes.put(user, 1);
                                            } else {
                                                busyNodes.put(callee, duration);
                                                userBusyCount.put(user, userBusyCount.getOrDefault(user, 0) + duration);
                                                userBusyCount.put(callee, userBusyCount.getOrDefault(callee, 0) + duration);
                                                durationTotal += duration;
                                                actualCalls++;
                                            }
                                            printCall(outputWriter, user, callee, duration, hour, minute, second);
                                            generatedCalls++;
                                            called = true;
                                            break;
                                        }
                                    }
                                    if (!called) {
                                        problem++;
                                        System.out.println(user);
                                        System.out.println(total);
                                        System.out.println(userConnections.get(user));
                                    }
                                }
                            }
                        }
                        wontMakeCallsAnyMore.forEach(willMakeCallInInterval::remove);
                        remainingTimeSize--;
                    }
                }
            }
            interval++;
            //System.out.println(totalTime / nCalls);
        }
        
        flushAndClose(outputWriter);
        //System.out.println("Generated " + generatedCalls + " calls");
        //System.out.println("Polled without callee: " + problem);
        //System.out.println("Average duration:" + durationTotal * 1.0 / actualCalls);
        //System.out.println(actualCalls);
    }
    
    //TODO remove this, it's completely useless other than it's fast to switch between writeln and printLine
    private void printLine(Writer writer, String string) throws IOException {
        System.out.println(string);
        writeln(writer, string);
    }
    
    private void printCall(Writer writer, int caller, int receiver, int duration, int hour, int minute, int second) throws IOException {
        String timestamp = String.format("%02d-%02d-%02d %02d:%02d:%02d", year, month, day, hour, minute, second);
        printLine(writer, caller + SEP + receiver + SEP + duration + SEP + timestamp);
        totalTime += IntervalHelper.calculateTimePoint(hour, minute, second);
        nCalls++;
    }
    
    public static void main(String[] args) throws IOException {
        
        ConfigReader.readConfig();
        
        AbstractUserFeatures clusteredUserFeatures = new ClusteredUserFeatures
                (CALLERS_LIST, PROFILE_TYPES, Constants.USER_COUNT, 35);
        AbstractUserFeatures personalUserFeatures = new PersonalUserFeatures
                (Constants.USER_COUNT, DatasetMain.USER_PROFILE_FILE);
        Generator generator = new Generator(personalUserFeatures);
        generator.init(Constants.USER_COUNT);
        
        generator.run(2017, 1, 3, 2);
        //System.out.println(QuantileDuration.map);
        QuantileDuration.map.clear();

//        generator.run(2017, 1, 3, 2);
//        System.out.println(QuantileDuration.map);
//        QuantileDuration.map.clear();
//
//        generator.run(2017, 1, 3, 2);
//        System.out.println(QuantileDuration.map);
//        QuantileDuration.map.clear();
//
//        generator.run(2017, 1, 3, 2);
//        System.out.println(QuantileDuration.map);
    }
    
}
