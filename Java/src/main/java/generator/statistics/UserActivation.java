package main.java.generator.statistics;

import main.java.configuration.Constants;
import main.java.dataset.intervals.CallIntervals;
import main.java.dataset.model.Tuple;
import main.java.generator.Generator;
import main.java.generator.GeneratorHelper;

import java.util.Map;

public class UserActivation {

    public static void main(String[] args) {

        long total = 0;
        CallIntervals.initialize();
        long[] userExpectedCalls = new long[]{1, 2, 5, 10, 20, 50, 100, 1000, 2000};
        long[][] userIntervalCallCount = new long[userExpectedCalls.length][Constants.INTERVAL_COUNT];
        for (int user = 0; user < userIntervalCallCount.length; user++) {
            for (int interval = 0; interval < userIntervalCallCount[0].length; interval++) {
                userIntervalCallCount[user][interval] = userExpectedCalls[user];
                total+= userExpectedCalls[user];
            }
        }

        int interval = 0;
        long generated = 0;
        for (Map.Entry<Tuple<Double>, String> intervalTimeName : CallIntervals.timeIntervalMap.entrySet()) {
            double startDecimalTime = intervalTimeName.getKey().getV1();
            double endDecimalTime = intervalTimeName.getKey().getV2();
            int timeSize = GeneratorHelper.calculateTimeSize(startDecimalTime, endDecimalTime);

            for (int user = 0; user < userIntervalCallCount.length; user++) {

                for (int hour = (int) startDecimalTime; hour < (int) endDecimalTime + 1; hour++) {

                    int startMinute = GeneratorHelper.intervalMinutesForCurrentHour(startDecimalTime, hour, true);
                    int endMinute = GeneratorHelper.intervalMinutesForCurrentHour(endDecimalTime, hour, false);
                    for (int minute = startMinute; minute < endMinute; minute++) {

                        for (int second = 0; second < 60; second++) {
                            if (userIntervalCallCount[user][interval] > 0) {
                                if (GeneratorHelper.activateUser(timeSize, userIntervalCallCount[user][interval])) {
                                    generated++;
                                    userIntervalCallCount[user][interval]--;
                                }
                            }
                        }
                    }
                }
            }
            interval++;
        }

        System.out.println(total);
        System.out.println(generated);

    }
    
}
