package main.java.generator;

import java.util.Random;

public class GeneratorHelper {

    public static final Random RANDOM = new Random();

    public static int minutesFromDecimal(double decimalTime) {
        int hours = (int) decimalTime;
        return (int) ((decimalTime - hours) * 60);
    }

    public static int intervalMinutesForCurrentHour(double decimalIntervalBorder, int currentHour, boolean start) {
        int intervalBorderHours = (int) (decimalIntervalBorder);
        if (currentHour == intervalBorderHours) {
            return minutesFromDecimal(decimalIntervalBorder);
        }
        if (start) {
            return 0;
        } else {
            return 60;
        }
    }

    public static long calculateExpectedCalls(double expectedCalls) {
        return calculateExpectedCalls(expectedCalls, 2);
    }

    public static int calculateTimeSize(double startDecimalTime, double endDecimalTime) {
        return (int) ((endDecimalTime - startDecimalTime) * 3600);
    }

    public static boolean activateUser(int timeSize, int remainingTimeSize, long userIntervalTotalCalls, long userIntervalCallsRemaining) {
        return activateUser(timeSize, remainingTimeSize, userIntervalTotalCalls, userIntervalCallsRemaining, 0);
    }

    public static boolean activateUser(int timeSize, int remainingTimeSize, long userIntervalTotalCalls, long userIntervalCallsRemaining, int userWasBusyInInterval) {
//        if (timeSize < userIntervalCallsRemaining || timeSize > 1) {
//            return true;
//        }
        return Math.random() * (userIntervalCallsRemaining > 20 ? userIntervalCallsRemaining * 1.0 / userIntervalTotalCalls : 1)
                <
                1.0 / (timeSize - userWasBusyInInterval + 1) * userIntervalTotalCalls;
    }

    public static long calculateExpectedCalls(double expectedCalls, float factor) {
        if (expectedCalls < 1) {
            double part = 0.25;
            long   n    = 0;
            expectedCalls -= part;
            while (expectedCalls > 0) {
                if (Math.random() < part) {
                    n++;
                }
                expectedCalls -= part;
            }
            expectedCalls += part;
            if (Math.random() < expectedCalls) {
                n++;
            }
            return n;
        }
        return uniformCallCountRandom(expectedCalls, factor);
    }

    public static long uniformCallCountRandom(double expectedCalls) {
        return uniformCallCountRandom(expectedCalls, 2);
    }

    public static long uniformCallCountRandom(double expectedCalls, float factor) {
        return Math.round(Math.random() * factor * expectedCalls);
    }

    public static long gaussCallCountRandom(double expectedCalls) {
        long r = -1;
        while (r < 0) {
            r = Math.round(RANDOM.nextGaussian() * expectedCalls + expectedCalls);
        }
        return r;
    }

}
