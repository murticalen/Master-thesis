package main.java.configuration;

import main.java.dataset.intervals.CallerTypesClustering;
import main.java.dataset.util.AbstractReader;

public final class Constants extends AbstractReader {

    /* *********************************************** *
     * *************** FINAL CONSTANTS *************** *
     * *********************************************** */
    public static final String SEPARATOR = ";";
    public static final int[] DAYS = {1, 2, 3, 4, 5, 6, 7};
    public static final int DAY_COUNT = DAYS.length;
    public static final String INPUT_CONFIG_LOCATION = "./input_config_picker.txt";

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

    //DURATION
    private static final int DEFAULT_DURATION_TYPE = 1;
    private static final int DEFAULT_DURATION_FEATURES = 9;
    private static final int DEFAULT_MIN_DURATION = 1;

    private static int DURATION_FEATURES = DEFAULT_DURATION_FEATURES;
    private static float PERCENTILE_STEP = 1.0f / (DURATION_FEATURES - 1);
    public static int DURATION_TYPE = DEFAULT_DURATION_TYPE;
    public static int MIN_DURATION = DEFAULT_MIN_DURATION;

    public static int getPercentileCount() {
        return DURATION_FEATURES;
    }

    public static float getPercentileStep() {
        return PERCENTILE_STEP;
    }

    public static void setDurationFeatures(int durationFeatures) {
        Constants.DURATION_FEATURES = durationFeatures;
        Constants.PERCENTILE_STEP = 1.0f / (DURATION_FEATURES - 1);
    }
}
