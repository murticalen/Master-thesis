package main.java.generator.determine_call_count;

import main.java.dataset.intervals.CallerTypesClustering;
import main.java.dataset.util.Constants;

import java.io.IOException;

public class PersonalCallCountDeterminator extends AbstractCallCountDeterminator {

    private double[][] userIntensitites;
    private String userFeaturesFile;

    public PersonalCallCountDeterminator(int userCount, String userFeaturesFile) {
        super(userCount);
        this.userFeaturesFile = userFeaturesFile;
    }

    @Override
    public void readData() throws IOException {
        userIntensitites = new double[CallerTypesClustering.TOTAL_USER_COUNT][Constants.TOTAL_DAY_INTERVAL_COUNT];
        readInputAndDoStuff(userFeaturesFile, line -> {
            String[] parts = line.split(SEP);
            int user = Integer.parseInt(parts[0]);
            int interval = Integer.parseInt(parts[1]) - 1;
            userIntensitites[user][interval] = Double.parseDouble(parts[2]);
        });
    }

    @Override
    public long[][] determineUserCallCount(int day, String expectedCallsFile) throws IOException {
        return generateExpectedCounts(expectedCallsFile, ((user, interval) -> userIntensitites[user][interval + Constants.INTERVAL_COUNT * day]));
    }

    @Override
    public void clearFeatures() {
        userFeaturesFile = null;
    }
}
