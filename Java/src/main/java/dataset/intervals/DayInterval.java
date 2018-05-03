package main.java.dataset.intervals;

import main.java.dataset.util.Tuple;

import java.util.LinkedHashMap;
import java.util.Map;

public class DayInterval {

    public static final String AFTER_MIDNIGHT = "After-midnight";
    public static final String LATE_NIGHT = "Late-night";
    public static final String EARLY_MORNING = "Early-morning";
    public static final String MORNING = "Morning";
    public static final String PRE_NOON = "Pre-noon";
    public static final String NOON = "Noon";
    public static final String AFTERNOON = "Afternoon";
    public static final String PRE_EVENING = "Pre-evening";
    public static final String EVENING = "Evening";
    public static final String LATE_EVENING = "Late-evening";

    public static final String[] INTERVALS = {
            AFTER_MIDNIGHT,
            LATE_NIGHT,
            EARLY_MORNING,
            MORNING,
            PRE_NOON,
            NOON,
            AFTERNOON,
            PRE_EVENING,
            EVENING,
            LATE_EVENING,
    };

    public static void initializeTimeIntervalMap() {
        Map<Tuple<Double>, String> timeIntervalMap = new LinkedHashMap<>();

        timeIntervalMap.put(new Tuple<>(0.0, 2.0), AFTER_MIDNIGHT);
        timeIntervalMap.put(new Tuple<>(2.0, 4.0), LATE_NIGHT);
        timeIntervalMap.put(new Tuple<>(4.0, 7.0), EARLY_MORNING);
        timeIntervalMap.put(new Tuple<>(7.0, 9.5), MORNING);
        timeIntervalMap.put(new Tuple<>(9.5, 11.0), PRE_NOON);
        timeIntervalMap.put(new Tuple<>(11.0, 13.5), NOON);
        timeIntervalMap.put(new Tuple<>(13.5, 16.0), AFTERNOON);
        timeIntervalMap.put(new Tuple<>(16.0, 19.0), PRE_EVENING);
        timeIntervalMap.put(new Tuple<>(19.0, 22.0), EVENING);
        timeIntervalMap.put(new Tuple<>(22.0, 24.0), LATE_EVENING);

        CallIntervals.timeIntervalMap = timeIntervalMap;
    }

}
