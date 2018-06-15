package main.java.test;

import main.java.dataset.intervals.IntervalHelper;
import main.java.dataset.model.CallRecord;
import main.java.dataset.util.Tuple;

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

}
