package main.java.dataset.intervals;

import main.java.dataset.util.CallRecord;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class DateFilter {

    public static final DateFormat DATE_FORMAT = new SimpleDateFormat("yy-MM-dd");
    public static final Set<String> ignoredDates = new HashSet<>();
    public static final Set<String> onlyValidDates = new HashSet<>();

    public static String extractDateWithCheck(Date timestamp) {
        String date = DATE_FORMAT.format(timestamp);
        if (!onlyValidDates.isEmpty() && !onlyValidDates.contains(date)) {
            throw new IllegalArgumentException();
        }
        if (ignoredDates.contains(date)) {
            throw new IllegalArgumentException();
        }
        return date;
    }

    public static boolean isValidDate(Date timestamp) {
        String date = DATE_FORMAT.format(timestamp);
        if (!onlyValidDates.isEmpty() && !onlyValidDates.contains(date)) {
            return false;
        }
        return !ignoredDates.contains(date);
    }

    public static boolean isValidDate(CallRecord record) {
        try {
            Date timestamp = CallRecord.TIMESTAMP_FORMATTER.parse(record.getCallTime());
            return isValidDate(timestamp);
        } catch (ParseException e) {
            throw new IllegalStateException(e);
        }
    }

}
