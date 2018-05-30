package main.java.test;

import main.java.dataset.intervals.CallIntervals;
import main.java.dataset.intervals.IntervalHelper;

public class Main {


    public static void main(String[] args) {
        CallIntervals.initialize();

        System.out.println(IntervalHelper.pdf(1)-IntervalHelper.cdf(0.5));
    }

}
