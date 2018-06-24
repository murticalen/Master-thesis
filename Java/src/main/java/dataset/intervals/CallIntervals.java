package main.java.dataset.intervals;

import main.java.dataset.model.Tuple;
import main.java.configuration.Constants;

import java.util.*;

public class CallIntervals {

    public static final String SEP = Constants.SEPARATOR;
    public static String ONE_LINE_INTERVAL_HEADER;
    public static String MULTI_LINE_INTERVAL_HEADER = "userId" + SEP + "dayIntervalId" + SEP + "totalProbability";
    public static Map<Integer, Map<String, Integer>> dayIntervalIdMap;
    public static Map<Tuple<Double>, String> timeIntervalMap;
    public static final Map<Integer, Set<String>> weekDayDateMap = new TreeMap<>();
    private static Map<String, Double> emptyMap = new HashMap<>();

    public static String getOneLineIntervalsString(Map<Integer, Map<String, Double>> intervals) {
        return getIntervalsString(intervals, CallIntervals.SEP, "", ((day, interval) -> ""));
    }

    public static String getMultiLineIntervalsString(Map<Integer, Map<String, Double>> intervals, int userId) {
        return getIntervalsString(intervals, System.lineSeparator(), userId + CallIntervals.SEP, ((day, interval) -> dayIntervalIdMap.get(day).get(interval) + CallIntervals.SEP));
    }

    private static String getIntervalsString(Map<Integer, Map<String, Double>> intervals, String separator, String prefix, PrefixCreator prefixCreator) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < Constants.DAYS.length; i++) {
            Map<String, Double> dayIntervalMap = intervals.getOrDefault(Constants.DAYS[i], emptyMap);

            for (int j = 0; j < Constants.INTERVAL_COUNT; j++) {
                String value = "0.0";
                if (dayIntervalMap.containsKey(Constants.INTERVALS[j])) {
                    double prob = dayIntervalMap.get(Constants.INTERVALS[j]);
                    if (prob > 1e-6) {
                        value = String.format("%.6f", prob);
                    }
                }
                sb.append(prefix);
                sb.append(prefixCreator.createPrefix(Constants.DAYS[i], Constants.INTERVALS[j]));
                sb.append(value);
                if (i != Constants.DAYS.length - 1 || j != Constants.INTERVAL_COUNT - 1) {
                    sb.append(separator);
                }
            }
        }
        return sb.toString();
    }

    private interface PrefixCreator {
        String createPrefix(int day, String interval);
    }

    public static void initialize() {
        initializeHeader();
        initializeTimeIntervalMap();
        initializeDayIntervalIdMap();
    }

    private static void initializeHeader() {
        StringBuilder oneLineHeader = new StringBuilder();
        for (int i = 0; i < Constants.DAYS.length; i++) {
            int day = Constants.DAYS[i];
            for (int j = 0; j < Constants.INTERVAL_COUNT; j++) {
                String interval = Constants.INTERVALS[j];
                oneLineHeader.append(day);
                oneLineHeader.append("-");
                oneLineHeader.append(interval);
                if (i != Constants.DAYS.length - 1 || j != Constants.INTERVAL_COUNT - 1) {
                    oneLineHeader.append(SEP);
                }
            }
        }
        ONE_LINE_INTERVAL_HEADER = oneLineHeader.toString();
    }

    private static void initializeTimeIntervalMap() {
        Map<Tuple<Double>, String> timeIntervalMap = new LinkedHashMap<>();

        timeIntervalMap.put(new Tuple<>(0.0, 2.0), Constants.AFTER_MIDNIGHT);
        timeIntervalMap.put(new Tuple<>(2.0, 4.0), Constants.LATE_NIGHT);
        timeIntervalMap.put(new Tuple<>(4.0, 7.0), Constants.EARLY_MORNING);
        timeIntervalMap.put(new Tuple<>(7.0, 9.5), Constants.MORNING);
        timeIntervalMap.put(new Tuple<>(9.5, 11.0), Constants.PRE_NOON);
        timeIntervalMap.put(new Tuple<>(11.0, 13.5), Constants.NOON);
        timeIntervalMap.put(new Tuple<>(13.5, 16.0), Constants.AFTERNOON);
        timeIntervalMap.put(new Tuple<>(16.0, 19.0), Constants.PRE_EVENING);
        timeIntervalMap.put(new Tuple<>(19.0, 22.0), Constants.EVENING);
        timeIntervalMap.put(new Tuple<>(22.0, 24.0), Constants.LATE_EVENING);

        CallIntervals.timeIntervalMap = timeIntervalMap;
    }

    private static void initializeDayIntervalIdMap() {
        int id = 0;
        CallIntervals.dayIntervalIdMap = new HashMap<>();
        for (int day : Constants.DAYS) {
            Map<String, Integer> map = new HashMap<>();
            for (String interval : Constants.INTERVALS) {
                id++;
                map.put(interval, id);
            }
            CallIntervals.dayIntervalIdMap.put(day, map);
            CallIntervals.weekDayDateMap.put(day, new TreeSet<>());
        }
    }
}
