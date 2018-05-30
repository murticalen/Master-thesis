package main.java.dataset.model;

import java.util.List;

public class CallDuration {

    public static class EmptyDuration extends AbstractCallDuration {
        @Override
        public int getCallDuration() {
            return random.nextInt(2000);
        }
    }

    public static class StatisticalDuration extends AbstractCallDuration {

        private List<Double> coefficients;

        public StatisticalDuration(List<Double> coefficients) {
            this.coefficients = coefficients;
        }

        @Override
        public int getCallDuration() {
            double x = Math.random();
            double sum = 0;
            for (int i = 0, size = coefficients.size() - 1; i < size; i++) {
                sum += coefficients.get(i) * x;
            };
            sum *= coefficients.get(coefficients.size() - 1);
            return (int)sum;
        }
    }

    public static AbstractCallDuration parseFromString(String line, String separator) {
        String[] parts = line.split(separator);
        int type = Integer.parseInt(parts[0]);
        switch (type) {
            default:
                return new EmptyDuration();
        }
    }

}
