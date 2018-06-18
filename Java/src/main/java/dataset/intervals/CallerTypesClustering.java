package main.java.dataset.intervals;

import main.java.dataset.DatasetMain;
import main.java.dataset.util.Constants;
import main.java.dataset.util.KMeans;
import main.java.dataset.util.KMeansWriter;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class CallerTypesClustering {

    public static final int USER_COUNT = 1434492;
    public static final int TOTAL_USER_COUNT = 2003241;
    public static final String SEP = Constants.SEPARATOR;
    public static final int ITERATIONS = 1000;
    public static final double MAX_ERROR = 10;
    public static final int CLUSTER_COUNT = 30;
    public static final int MIN_CLUSTER = 2;

    public void run() throws IOException {
        //since this only analyses the callers, we need to remap ids (identity function for caller and receiver) to just callers
        Map<Integer, Integer> ids = new HashMap<>(Constants.USER_COUNT);
        double[][] data = new double[Constants.USER_COUNT][Constants.TOTAL_DAY_INTERVAL_COUNT];
        double[] featuresMax = new double[Constants.TOTAL_DAY_INTERVAL_COUNT];
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

        for (int i = 0; i < Constants.USER_COUNT; i++) {
            for (int j = 0; j < Constants.TOTAL_DAY_INTERVAL_COUNT; j++) {
                data[i][j] = data[i][j] / featuresMax[j];
            }
        }

        reader.close();

//        for (int i = 0; i < FEATURES_COUNT; i++) {
//            System.err.println(String.format("%2d: %f", i, featuresMax[i]));
//        }

        double[][] slicedData = new double[Constants.USER_COUNT][Constants.INTERVAL_COUNT];

        KMeans kMeans = new KMeans(Constants.INTERVAL_COUNT, ITERATIONS, MAX_ERROR, Constants.USER_COUNT);
        KMeansWriter kMeansWriter = new KMeansWriter(kMeans, Constants.SEPARATOR, "user");
        System.err.println("kMeans");

        //NOTE: this is excruciatingly long process
        for (int day = 0; day < Constants.DAY_COUNT; day++) {
            for (int user = 0; user < Constants.USER_COUNT; user++) {
                slicedData[user] = Arrays.copyOfRange(data[user], day * Constants.INTERVAL_COUNT, Constants.INTERVAL_COUNT + day * Constants.INTERVAL_COUNT);
            }
            for (int k = 35; k <= 35; k++) {
                kMeans.run(slicedData, k);
                kMeansWriter.writeCentroids("./../dataset/centroids" + k + ".csv", featuresMax);
                kMeansWriter.writeDataCluster("./../dataset/dataCluster" + k + ".csv");
            }
            break;
        }
    }

    public static void main(String[] args) throws IOException {
        //System.setOut(new PrintStream(new File("./../test_results/monday_kmeans_java.csv")));
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
