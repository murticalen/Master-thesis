package main.java.dataset.intervals;

import main.java.dataset.DatasetMain;
import main.java.dataset.model.CallRecord;
import main.java.dataset.util.KMeans;
import main.java.dataset.util.KMeansWriter;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class CallerTypesClustering {

    public static final int USER_COUNT = 1434492;
    public static final String SEP = CallRecord.SEP;
    public static final int DAY_COUNT = DayInterval.DAYS.length;
    public static final int INTERVAL_COUNT = DayInterval.INTERVALS.length;
    public static final int FEATURES_COUNT = DAY_COUNT * INTERVAL_COUNT;
    public static final int ITERATIONS = 1000;
    public static final double MAX_ERROR = 10;
    public static final int CLUSTER_COUNT = 30;
    public static final int MIN_CLUSTER = 2;
    public static final String[] DAYS = {"Mon", "Tue-Fri", "Sat", "Sun"};

    public void run() throws IOException {
        //since this only analyses the callers, we need to remap ids (identity function for caller and receiver) to just callers
        Map<Integer, Integer> ids = new HashMap<>(USER_COUNT);
        double[][] data = new double[USER_COUNT][FEATURES_COUNT];
        double[] featuresMax = new double[FEATURES_COUNT];
        BufferedReader reader = Files.newBufferedReader(Paths.get(DatasetMain.USER_PROFILE_FILE));
        String line = reader.readLine();
        int order = 0;

        while ((line = reader.readLine()) != null) {
            String[] splitLine = line.split(SEP);
            int user = Integer.parseInt(splitLine[0]) - 1;
            if (!ids.containsKey(user)) {
                ids.put(user, order);
                order++;
            }
            int feature = Integer.parseInt(splitLine[1]) - 1;
            double featureValue = Double.parseDouble(splitLine[2]);
            data[ids.get(user)][feature] = featureValue;
            featuresMax[feature] = Math.max(featuresMax[feature], featureValue);
        }

        for (int i = 0; i < USER_COUNT; i++) {
            for (int j = 0; j < FEATURES_COUNT; j++) {
                data[i][j] = data[i][j] / featuresMax[j];
            }
        }

        reader.close();
        double[][] slicedData = new double[USER_COUNT][INTERVAL_COUNT];

        KMeans kMeans = new KMeans(INTERVAL_COUNT, 1000, MAX_ERROR, USER_COUNT);
        KMeansWriter kMeansWriter = new KMeansWriter(kMeans, CallRecord.SEP);
        System.err.println("kMeans");

        //NOTE: this is excruciatingly long process
        for (int day = 0; day < DAY_COUNT; day++) {
            for (int user = 0; user < USER_COUNT; user++) {
                slicedData[user] = Arrays.copyOfRange(data[user], day * INTERVAL_COUNT, INTERVAL_COUNT + day * INTERVAL_COUNT);
            }
            for (int k = 35; k <= 35; k++) {
                kMeans.run(slicedData, k);
                kMeansWriter.writeCentroids("./../dataset/centroids"+k+".csv", featuresMax);
                kMeansWriter.writeDataCluster("./../dataset/dataCluster"+k+".csv");
            }
            break;
        }
    }

    public static void main(String[] args) throws IOException {
        System.setOut(new PrintStream(new File("./../test_results/monday_kmeans_java.csv")));
        CallerTypesClustering clustering = new CallerTypesClustering();
        clustering.run();

//        double[][] data = {
//                {Math.random(), Math.random()},};
//        KMeans kMeans = new KMeans(2, 20, 1e-9, data.length);
//        int[] clusters = kMeans.run(data, 8);
//        System.setOut(new PrintStream(new File("./../dataset/test.csv")));
//        System.out.println("x;y;cluster");
//        for (int i = 0; i < data.length; i++) {
//            double[] point = data[i];
//            System.out.println(point[0] + ";" + point[1] + ";" + clusters[i]);
//        }
    }

}
