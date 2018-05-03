package main.java.dataset.intervals;

import main.java.dataset.util.CallRecord;
import main.java.dataset.util.Tuple;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

public class CallIntervals {

    public static final String SEP = CallRecord.SEP;
    public static String INTERVAL_HEADER;
    public static final int[] DAYS = {1, 2, 3, 4, 5, 6, 7};
    public static final String[] INTERVALS = DayInterval.INTERVALS;
    public static Map<Tuple<Double>, String> timeIntervalMap;
    private static Map<String, Double> emptyMap = new HashMap<>();

    private CallIntervals() {

    }

    public static String getIntervalsString(CallRecord record) throws ParseException {
        StringBuilder sb = new StringBuilder();
        Map<Integer, Map<String, Double>> intervals = IntervalHelper.extractIntervals(record);
        for (int i = 0; i < DAYS.length; i++) {
            int day = DAYS[i];
            Map<String, Double> dayIntervalMap = intervals.getOrDefault(day, emptyMap);

            for (int j = 0; j < INTERVALS.length; j++) {
                sb.append(dayIntervalMap.getOrDefault(INTERVALS[j], 0.0));
                if (i != DAYS.length - 1 || j != INTERVALS.length - 1) {
                    sb.append(SEP);
                }
            }
        }
        return sb.toString();
    }

    public static void initialize() {
        initializeHeader();
        DayInterval.initializeTimeIntervalMap();
    }

    private static void initializeHeader() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < DAYS.length; i++) {
            int day = DAYS[i];
            for (int j = 0; j < INTERVALS.length; j++) {
                String interval = INTERVALS[j];
                sb.append(day);
                sb.append("-");
                sb.append(interval);
                if (i != DAYS.length - 1 || j != INTERVALS.length - 1) {
                    sb.append(SEP);
                }
            }
        }
        INTERVAL_HEADER = sb.toString();
    }
}
