package main.java.dataset.intervals;

import main.java.dataset.model.CallRecord;
import main.java.dataset.model.Tuple;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public final class IntervalHelper {

    private static final Calendar calendar = Calendar.getInstance();

    public static Map<Integer, Map<String, Double>> extractIntervals(CallRecord record) {
        Map<Integer, Map<String, Double>> result = new HashMap<>();
        double timePoint;
        try {
            timePoint = IntervalHelper.calculateTimePointAndStoreDateIfValid(record);
        } catch (IllegalArgumentException e) {
            return result;
        }
        int currentDay = record.getWeekDay();
        int previousDay = calculatePreviousDay(currentDay);
        int nextDay = calculateNextDay(currentDay);

        result.put(currentDay, new LinkedHashMap<>());
        result.put(previousDay, new LinkedHashMap<>());
        result.put(nextDay, new LinkedHashMap<>());

        for (Map.Entry<Tuple<Double>, String> intervalTimeName : CallIntervals.timeIntervalMap.entrySet()) {
            calculateProbabilityIfValid(result, currentDay, intervalTimeName.getKey(), intervalTimeName.getValue(), timePoint, 0.0);
            calculateProbabilityIfValid(result, previousDay, intervalTimeName.getKey(), intervalTimeName.getValue(), timePoint, -24.0);
            calculateProbabilityIfValid(result, nextDay, intervalTimeName.getKey(), intervalTimeName.getValue(), timePoint, 24.0);
        }

        return result;
    }

    private static void calculateProbabilityIfValid(Map<Integer, Map<String, Double>> result, int day, Tuple<Double> intervalTimes, String interval, double timePoint, double offset) {
        if (result.containsKey(day)) {
            double probability = IntervalHelper.intervalProbability(intervalTimes, timePoint, offset);
            result.get(day).put(interval, probability);
        }
    }

    public static double intervalProbability(Tuple<Double> intervalTimes, double timePoint, double offset) {
        return IntervalHelper.cdf(intervalTimes.getV2() + offset, timePoint) - IntervalHelper.cdf(intervalTimes.getV1() + offset, timePoint);
    }

    public static int calculatePreviousDay(int day) {
        return day != 1 ? day - 1 : 7;
    }

    public static int calculateNextDay(int day) {
        return day != 7 ? day + 1 : 1;
    }

    public static double calculateTimePoint(Date timestamp) {
        calendar.setTime(timestamp);
        int hours = calendar.get(Calendar.HOUR_OF_DAY);
        double minutes = calendar.get(Calendar.MINUTE);
        double seconds = calendar.get(Calendar.SECOND);

        return calculateTimePoint(hours, minutes, seconds);
    }

    public static double calculateTimePoint(int hours, double minutes, double seconds) {
        return hours + minutes / 60 + seconds / 3600;
    }

//    public static double calculateTimePointAndStoreDateIfValid(String timestamp, DateFormat format) {
//        try {
//            Date date = format.parse(timestamp);
//            return calculateTimePoint(date);
//        } catch (ParseException ex) {
//            throw new IllegalStateException(ex);
//        }
//    }

    private static double calculateTimePointAndStoreDateIfValid(CallRecord record) {
        try {
            Date timestamp = CallRecord.TIMESTAMP_FORMATTER.parse(record.getCallTime());
            //store how many days of this type there was, ignoring invalid dates
            String date = DateFilter.extractDateWithCheck(timestamp);
            CallIntervals.weekDayDateMap.get(record.getWeekDay()).add(date);
            return calculateTimePoint(timestamp);
        } catch (ParseException ex) {
            throw new IllegalStateException(ex);
        }
    }

    // return pdf(x) = standard Gaussian pdf
    public static double pdf(double x) {
        return Math.exp(-x * x / 2) / Math.sqrt(2 * Math.PI);
    }

    // return pdf(x, mu, signma) = Gaussian pdf with mean mu and stddev sigma
    public static double pdf(double x, double mu, double sigma) {
        return pdf((x - mu) / sigma) / sigma;
    }

    // return cdf(z) = standard Gaussian cdf using Taylor approximation
    public static double cdf(double z) {
        if (z < -8.0) return 0.0;
        if (z > 8.0) return 1.0;
        double sum = 0.0, term = z;
        for (int i = 3; sum + term != sum; i += 2) {
            sum = sum + term;
            term = term * z * z / i;
        }
        return 0.5 + sum * pdf(z);
    }

    // return cdf(z, mu, sigma) = Gaussian cdf with mean mu and stddev 1
    public static double cdf(double z, double mu) {
        return cdf(z, mu, 1);
    }

    // return cdf(z, mu, sigma) = Gaussian cdf with mean mu and stddev sigma
    public static double cdf(double z, double mu, double sigma) {
        return cdf((z - mu) / sigma);
    }

}
