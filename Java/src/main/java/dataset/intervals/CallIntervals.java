package main.java.dataset.intervals;

import main.java.dataset.util.CallRecord;

public class CallIntervals {

    public static final int TOTAL_COUNT = 2;
    public static final String SEP = CallRecord.SEP;
    public static String INTERVAL_HEADER;
    public static final int[] DAYS = {1, 2, 3, 4, 5, 6, 7};
    public static final String[] INTERVALS = {"Midnight", "Late-night", "Early morning", "Morning", "Pre-noon", "Noon", "Afternoon", "Pre-evening", "Evening", "Late-evening"};


    private CallIntervals() {

    }

    public static String extractIntervals(CallRecord record) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < DAYS.length; i++) {
            int day = DAYS[i];
            for (int j = 0; j < INTERVALS.length; j++) {

                sb.append(0.0055);

                if (i != DAYS.length - 1 || j != INTERVALS.length - 1) {
                    sb.append(SEP);
                }
            }
        }
        return sb.toString();
    }

    public static void initializeHeader() {
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
