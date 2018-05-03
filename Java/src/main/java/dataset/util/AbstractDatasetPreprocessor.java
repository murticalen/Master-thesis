package main.java.dataset.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Map;

public abstract class AbstractDatasetPreprocessor extends AbstractReader {

    protected DateFormat inputDateFormatter;
    protected DateFormat weekdayFormatter;
    protected Map<String, Integer> weekDays;

    protected AbstractDatasetPreprocessor() {
        this.weekDays = Map.of("Mon", 1, "Tue", 2, "Wed", 3, "Thu", 4, "Fri", 5, "Sat", 6, "Sun", 7);
    }

    public abstract void preProcessDataset(String inputPath, String outputFile) throws Exception;

    protected String sanitize(String input) {
        return input.replaceAll("\"", "");
    }

    protected int calculateWeekday(String date) {
        try {
            return weekDays.get(this.weekdayFormatter.format(this.inputDateFormatter.parse(date)));
        } catch (ParseException exception) {
            System.err.println("Wrong input format " + date);
            return 0;
        }
    }

    protected String getTimestamp(String date) {
        try {
            return CallRecord.TIMESTAMP_FORMATTER.format(this.inputDateFormatter.parse(date));
        } catch (ParseException exception) {
            System.err.println("Wrong input format " + date);
            return date;
        }
    }
}
