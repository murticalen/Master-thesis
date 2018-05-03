package main.java.dataset.intervals;

import main.java.dataset.util.CallRecord;
import main.java.dataset.util.Tuple;

import java.text.ParseException;
import java.util.*;

public final class IntervalHelper {

    private static final Calendar calendar = Calendar.getInstance();

    public static Map<Integer, Map<String, Double>> extractIntervals(CallRecord record) throws ParseException {
        Map<Integer, Map<String, Double>> result = new HashMap<>();
        double timePoint = IntervalHelper.calculateTimePoint(record.getCallTime());
        int currentDay = record.getWeekDay();
        int previousDay = calculatePreviousDay(currentDay);
        int nextDay = calculateNextDay(currentDay);

        result.put(currentDay, new LinkedHashMap<>());
        result.put(previousDay, new LinkedHashMap<>());
        result.put(nextDay, new LinkedHashMap<>());

        for (var timeIntervalEntry : CallIntervals.timeIntervalMap.entrySet()) {
            double currentDayProb = IntervalHelper.intervalProbability(timeIntervalEntry.getKey(), timePoint, 0.0);
            double previousDayProb = IntervalHelper.intervalProbability(timeIntervalEntry.getKey(), timePoint, -24.0);
            double nextDayProb = IntervalHelper.intervalProbability(timeIntervalEntry.getKey(), timePoint, 24.0);

            result.get(currentDay).put(timeIntervalEntry.getValue(), currentDayProb);
            result.get(previousDay).put(timeIntervalEntry.getValue(), previousDayProb);
            result.get(nextDay).put(timeIntervalEntry.getValue(), nextDayProb);
        }

        return result;
    }

    public static double intervalProbability(Tuple<Double> tuple, double timePoint, double offset) {
        return IntervalHelper.cdf(tuple.getV2() + offset, timePoint) - IntervalHelper.cdf(tuple.getV1() + offset, timePoint);
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
        return hours + minutes / 60 + seconds / 3600;
    }

    public static double calculateTimePoint(String timestamp) throws ParseException {
        return calculateTimePoint(CallRecord.TIMESTAMP_FORMATTER.parse(timestamp));
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

    // Compute z such that cdf(z) = y via bisection search
    public static double inverseCDF(double y) {
        return inverseCDF(y, 0.00000001, -8, 8);
    }

    // bisection search
    private static double inverseCDF(double y, double delta, double lo, double hi) {
        double mid = lo + (hi - lo) / 2;
        if (hi - lo < delta) return mid;
        if (cdf(mid) > y) return inverseCDF(y, delta, lo, mid);
        else return inverseCDF(y, delta, mid, hi);
    }

}
