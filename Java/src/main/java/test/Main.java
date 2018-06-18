package main.java.test;

import main.java.dataset.intervals.CallIntervals;
import main.java.dataset.intervals.IntervalHelper;

public class Main {


    public static void main(String[] args) {
        CallIntervals.initialize();

        //HelperTest.testMinutesFromDecimal();
        //HelperTest.testTimeThroughDay();

        //IntervalHelperEvaluator.cdfTest();
        //IntervalHelperEvaluator.cdrTest();

        IntervalHelperEvaluator.callTotalTest();
    }

}
