package main.java.dataset.intervals;

import main.java.dataset.util.CallRecord;
import main.java.dataset.util.Tuple;

import java.util.*;

public class CallIntervals {

    public static final String SEP = CallRecord.SEP;
    public static String ONE_LINE_INTERVAL_HEADER;
    public static String MULTI_LINE_INTERVAL_HEADER = "userId" + SEP + "dayIntervalId" + SEP + "totalProbability";
    public static final int[] DAYS = DayInterval.DAYS;
    public static final String[] INTERVALS = DayInterval.INTERVALS;
    public static Map<Integer, Map<String, Integer>> dayIntervalIdMap;
    public static Map<Tuple<Double>, String> timeIntervalMap;
    public static final Map<Integer, Set<String>> weekDayDateMap = new HashMap<>();
    private static Map<String, Double> emptyMap = new HashMap<>();

    public static String getOneLineIntervalsString(Map<Integer, Map<String, Double>> intervals) {
        return getIntervalsString(intervals, CallIntervals.SEP, "", ((day, interval) -> ""));
    }

    public static String getMultiLineIntervalsString(Map<Integer, Map<String, Double>> intervals, int userId) {
        return getIntervalsString(intervals, System.lineSeparator(), userId + CallIntervals.SEP, ((day, interval) -> dayIntervalIdMap.get(day).get(interval) + CallIntervals.SEP));
    }

    private static String getIntervalsString(Map<Integer, Map<String, Double>> intervals, String separator, String prefix, PrefixCreator prefixCreator) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < DAYS.length; i++) {
            Map<String, Double> dayIntervalMap = intervals.getOrDefault(DAYS[i], emptyMap);

            for (int j = 0; j < INTERVALS.length; j++) {
                String value = "0.0";
                if (dayIntervalMap.containsKey(INTERVALS[j])) {
                    double prob = dayIntervalMap.get(INTERVALS[j]);
                    if (prob > 1e-6) {
                        value = String.format("%.6f", prob);
                    }
                }
                sb.append(prefix);
                sb.append(prefixCreator.createPrefix(DAYS[i], INTERVALS[j]));
                sb.append(value);
                if (i != DAYS.length - 1 || j != INTERVALS.length - 1) {
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
        for (int i = 0; i < DAYS.length; i++) {
            int day = DAYS[i];
            for (int j = 0; j < INTERVALS.length; j++) {
                String interval = INTERVALS[j];
                oneLineHeader.append(day);
                oneLineHeader.append("-");
                oneLineHeader.append(interval);
                if (i != DAYS.length - 1 || j != INTERVALS.length - 1) {
                    oneLineHeader.append(SEP);
                }
            }
        }
        ONE_LINE_INTERVAL_HEADER = oneLineHeader.toString();
    }

    private static void initializeTimeIntervalMap() {
        Map<Tuple<Double>, String> timeIntervalMap = new LinkedHashMap<>();

        timeIntervalMap.put(new Tuple<>(0.0, 2.0), DayInterval.AFTER_MIDNIGHT);
        timeIntervalMap.put(new Tuple<>(2.0, 4.0), DayInterval.LATE_NIGHT);
        timeIntervalMap.put(new Tuple<>(4.0, 7.0), DayInterval.EARLY_MORNING);
        timeIntervalMap.put(new Tuple<>(7.0, 9.5), DayInterval.MORNING);
        timeIntervalMap.put(new Tuple<>(9.5, 11.0), DayInterval.PRE_NOON);
        timeIntervalMap.put(new Tuple<>(11.0, 13.5), DayInterval.NOON);
        timeIntervalMap.put(new Tuple<>(13.5, 16.0), DayInterval.AFTERNOON);
        timeIntervalMap.put(new Tuple<>(16.0, 19.0), DayInterval.PRE_EVENING);
        timeIntervalMap.put(new Tuple<>(19.0, 22.0), DayInterval.EVENING);
        timeIntervalMap.put(new Tuple<>(22.0, 24.0), DayInterval.LATE_EVENING);

        CallIntervals.timeIntervalMap = timeIntervalMap;
    }

    private static void initializeDayIntervalIdMap() {
        int id = 0;
        CallIntervals.dayIntervalIdMap = new HashMap<>();
        for (int day : CallIntervals.DAYS) {
            Map<String, Integer> map = new HashMap<>();
            for (String interval : INTERVALS) {
                id++;
                map.put(interval, id);
            }
            CallIntervals.dayIntervalIdMap.put(day, map);
            CallIntervals.weekDayDateMap.put(day, new HashSet<>());
        }
    }
}
