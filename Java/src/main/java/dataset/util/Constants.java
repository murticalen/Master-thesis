package main.java.dataset.util;

import main.java.dataset.intervals.CallerTypesClustering;

import java.io.IOException;

public final class Constants extends AbstractReader {

    /* *********************************************** *
     * *************** FINAL CONSTANTS *************** *
     * *********************************************** */
    public static final String SEPARATOR = ";";
    public static final int[] DAYS = {1, 2, 3, 4, 5, 6, 7};
    public static final int DAY_COUNT = DAYS.length;

    /* ************************************************ *
     * ************** DEFAULT INTERVALS *************** *
     * ************************************************ */

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

    /* ************************************************ *
     * ************ CONFIGURABLE CONSTANTS ************ *
     * ************************************************ */
    public static int USER_COUNT = CallerTypesClustering.USER_COUNT;
    public static int INTERVAL_COUNT = INTERVALS.length;
    public static final int TOTAL_DAY_INTERVAL_COUNT = DAY_COUNT * INTERVAL_COUNT;

    public static void readFromFile(String file) throws IOException {

        if (file == null) {
            return;
        }

        readInputAndDoStuff(file, line -> {
            String[] parts = line.split(SEPARATOR);
            switch (parts[0]) {
                case "user-count":
                    USER_COUNT = Integer.parseInt(parts[1]);
                    break;
                case "interval-count":
            }
        });
    }

}
