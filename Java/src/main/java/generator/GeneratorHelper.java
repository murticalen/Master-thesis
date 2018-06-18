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
        if (expectedCalls < 5) {
            return GeneratorHelper.uniformCallCountRandom(expectedCalls);
        } else {
            return GeneratorHelper.uniformCallCountRandom(expectedCalls);
        }
    }

    public static long uniformCallCountRandom(double expectedCalls) {
        return uniformCallCountRandom(expectedCalls, 2);
    }

    public static long uniformCallCountRandom(double expectedCalls, float factor) {
        if (expectedCalls < 0.4) {
            if (Math.random() < expectedCalls) {
                return 1;
            }
        }
        return Math.round(Math.random() * factor * expectedCalls);
    }

    public static long gaussCallCountRandom(double expectedCalls) {
        return Math.round(RANDOM.nextGaussian() * expectedCalls + expectedCalls);
    }

}
