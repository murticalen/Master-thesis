package main.java.generator.duration;

import main.java.configuration.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public abstract class AbstractCallDuration {

    protected static Random random = new Random();

    public abstract int getCallDuration();

    public static void parseFromString(Map<Integer, AbstractCallDuration> userDurations, String line, String separator) {
        String[] parts = line.split(separator);
        switch (Constants.DURATION_TYPE) {
            case 1:
                List<Float> percentiles = new ArrayList<>();
                for (int i = 1; i < parts.length; i++) {
                    percentiles.add(Float.parseFloat(parts[i]));
                }
                userDurations.put(Integer.parseInt(parts[0]), new QuantileDuration(percentiles));
                break;
        }
    }

}
