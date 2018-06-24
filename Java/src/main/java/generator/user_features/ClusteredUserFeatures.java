package main.java.generator.user_features;

import main.java.configuration.Constants;

import java.io.IOException;

public class ClusteredUserFeatures extends AbstractUserFeatures {

    private String userProfilesFile;
    private String profileFeaturesFile;
    private int[] userProfiles;
    private double[][] profileFeatures;
    private int userCount;
    private int profilesCount;

    public ClusteredUserFeatures(String userProfilesFile, String profileFeaturesFile, int userCount, int profilesCount) {
        super(userCount);
        this.userProfilesFile = userProfilesFile;
        this.profileFeaturesFile = profileFeaturesFile;
        this.userCount = userCount;
        this.profilesCount = profilesCount;
    }

    public void readData() throws IOException {
        userProfiles = new int[userCount];
        readInputAndDoStuff(userProfilesFile, line -> {
            String[] parts = line.split(SEP);
            userProfiles[Integer.parseInt(parts[0])] = Integer.parseInt(parts[1]);
        });

        profileFeatures = new double[profilesCount][Constants.INTERVAL_COUNT];
        readInputAndDoStuff(profileFeaturesFile, line -> {
            String[] parts = line.split(SEP);
            int cluster = Integer.parseInt(parts[0]);
            int feature = Integer.parseInt(parts[1]);
            profileFeatures[cluster][feature] = Double.parseDouble(parts[2]);
        });
    }

    @Override
    public long[][] determineUserCallCount(int day, String expectedCallsFile) throws IOException {
        return generateExpectedCounts(expectedCallsFile, (user, interval) -> profileFeatures[userProfiles[user]][interval]);
    }

    @Override
    public void clearFeatures() {
        profileFeatures = null;
        userProfiles = null;
    }
}
