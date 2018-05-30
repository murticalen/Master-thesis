package main.java.dataset.util;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.LinkedHashMap;
import java.util.Map;

public abstract class AbstractDatasetPreprocessor extends AbstractReader {

    protected DateFormat inputDateFormatter;
    protected DateFormat weekdayFormatter;
    protected Map<String, Integer> weekDays;

    protected AbstractDatasetPreprocessor() {
        weekDays = new LinkedHashMap<>();
        weekDays.put("Mon", 1);
        weekDays.put("Tue", 2);
        weekDays.put("Wed", 3);
        weekDays.put("Thu", 4);
        weekDays.put("Fri", 5);
        weekDays.put("Sat", 6);
        weekDays.put("Sun", 7);
    }

    public abstract void preProcessData(String inputPath, String outputFile) throws IOException, ParseException;

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
