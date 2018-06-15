package main.java.test;

import main.java.dataset.intervals.CallIntervals;
import main.java.dataset.util.Tuple;
import main.java.generator.Generator;

import java.util.Map;

public class HelperTest {

    public static void testMinutesFromDecimal() {

        if (0 != Generator.minutesFromDecimal(9.0)) {
            throw new AssertionError();
        } else {
            System.err.println("Test correct");
        }

        if (30 != Generator.minutesFromDecimal(9.5)) {
            throw new AssertionError();
        } else {
            System.err.println("Test correct");
        }

    }

    public static void testTimeThroughDay() {

        CallIntervals.initialize();

        boolean[][] hourMinutePassed = new boolean[24][60];
        for (int i = 0; i < 23; i++) {
            for (int j = 0; j < 60; j++) {
                hourMinutePassed[i][j] = false;
            }
        }

        //hourMinutePassed[11][3] = true;

        for (Map.Entry<Tuple<Double>, String> tupleStringEntry : CallIntervals.timeIntervalMap.entrySet()) {

            double startDecimalTime = tupleStringEntry.getKey().getV1();
            double endDecimalTime = tupleStringEntry.getKey().getV2();

            for (int hour = (int) startDecimalTime; hour < (int) endDecimalTime + 1; hour++) {

                int startMinute = Generator.minutesForInterval(startDecimalTime, hour, true);
                int endMinute = Generator.minutesForInterval(endDecimalTime, hour, false);
                for (int minute = startMinute; minute < endMinute; minute++) {
                    if (!hourMinutePassed[hour][minute]) {
                        hourMinutePassed[hour][minute] = true;
                    } else {
                        throw new AssertionError(String.format("Hour %d Minute %d", hour, minute));
                    }
                }
            }
        }

        for (int hour = 0; hour < 23; hour++) {
            for (int minute = 0; minute < 60; minute++) {
                if (!hourMinutePassed[hour][minute]) {
                    throw new AssertionError(String.format("Hour %d Minute %d", hour, minute));
                }
            }
        }

        System.err.println("Test correct");
    }

    public static void main(String[] args) {
        testMinutesFromDecimal();
        testTimeThroughDay();
    }

}
