package main.java.test;

import main.java.dataset.intervals.IntervalHelper;
import main.java.dataset.model.CallRecord;
import main.java.dataset.model.Tuple;

import java.util.Random;

public class IntervalHelperEvaluator {

    public static void cdfTest() {
        Tuple<Double> t = new Tuple<>(9.5, 10.5);
        System.out.println(IntervalHelper.intervalProbability(new Tuple<>(9.5, 10.5), 9.8, 0));
    }

    public static void cdrTest() {
        String time = "17-12-01 12:45:55";
        CallRecord record = new CallRecord("4", 1, 2, 55, time, 2);
        System.out.println(IntervalHelper.extractIntervals(record));
    }

    public static void callTotalTest() {
        double totalError = 0;
        double totalExpected = 0;
        long totalGenerated = 0;
        for (int j = 0; j < 800; j++) {
            Random random = new Random();
            double expectedCallCount = 0.6;
            int n = 10000;
            long total = 0;
            for (int i = 0; i < n; i++) {
                long generated = Math.max(Math.round(random.nextDouble() * 4 * expectedCallCount)/2, 0);
                total += generated;
                totalGenerated += generated;
                totalExpected += expectedCallCount;
            }
            //System.out.println(total / n);
            double error = expectedCallCount - total * 1.0 / n;
            totalError += error;
            System.out.println(error);
        }
        System.out.println(totalError);
        System.out.println("--------------------------");
        System.out.println(totalExpected);
        System.out.println(totalGenerated);
        long difference = (long)totalExpected - totalGenerated;
        System.out.println(difference*1.0/totalExpected * 100);
    }

}
