package main.java.generator.duration;

import main.java.configuration.Constants;

import java.util.HashMap;
import java.util.List;

public class QuantileDuration extends AbstractCallDuration {

    private List<Float> percentiles;
    public static HashMap<Integer, Integer> map = new HashMap<>();

    public QuantileDuration(List<Float> percentiles) {
        this.percentiles = percentiles;
    }

    @Override
    public int getCallDuration() {
        return calculateDuration(percentiles.get(getRandomQuantilePosition()));
    }

    private int calculateDuration(float percentile) {
        int duration = -1;
        int n = 0;
        //infinite loop shouldn't happen, but in case it does, avoid it by counting to 20 tries
        while (duration < 1 && n < 20) {
            duration = Math.round(random.nextFloat() * 2 * percentile);
            n++;
        }
        if (duration < Constants.MIN_DURATION) {
            duration = Constants.MIN_DURATION;
        }
        return duration;
    }

    public static int getRandomQuantilePosition() {
        //return random.nextInt(Constants.getPercentileCount());
        double nthPercentile;
        do {
            nthPercentile = (random.nextGaussian() * 0.25 + 0.5);
        } while (nthPercentile < 0 || nthPercentile > 1);
        final double floatPosition = nthPercentile / Constants.getPercentileStep();
        final int roundedPosition = (int) floatPosition;
        final double positionOffset = floatPosition - roundedPosition;
        if (positionOffset < 0.5) {
            map.put(roundedPosition, map.getOrDefault(roundedPosition, 0) + 1);
            return roundedPosition;
        }
        map.put(roundedPosition + 1, map.getOrDefault(roundedPosition + 1, 0) + 1);
        return roundedPosition + 1;
    }

}
