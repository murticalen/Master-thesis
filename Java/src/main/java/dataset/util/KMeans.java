package main.java.dataset.util;

import java.util.Random;

public class KMeans {

    private final int featuresCount;
    private final int iterations;
    private final Random rand = new Random(20);
    private double maxError = 0;
    private int pointCount;

    private double[][] centroids;
    private int[] dataCluster;
    private double error;
    private boolean converged;

    public KMeans(int featuresCount, int iterations, double maxError, int pointCount) {
        this.featuresCount = featuresCount;
        this.iterations = iterations;
        this.maxError = maxError;
        this.pointCount = pointCount;
    }

    public void run(double[][] data, int clusterCount) {

        int[] dataCluster = new int[pointCount];

        //initialize centroids;
        double[][] centroids = new double[clusterCount][featuresCount];
        int[] pointsInCluster = new int[clusterCount];
        for (int i = 0; i < clusterCount; i++) {
            centroids[i] = data[rand.nextInt(pointCount)];
        }

        //initialize clusters
        for (int point = 0; point < pointCount; point++) {
            int cluster = rand.nextInt(clusterCount);
            dataCluster[point] = cluster;
            pointsInCluster[cluster]++;
        }

        //run k-means
        boolean changed = true;
        int iteration = 0;
        double totalError = Double.MAX_VALUE;

        while (iteration < iterations && changed && totalError > maxError) {
            totalError = 0;
            changed = false;

            for (int point = 0; point < pointCount; point++) {
                double minUserError = distance(data[point], centroids[dataCluster[point]]);

                for (int cluster = 0; cluster < clusterCount; cluster++) {
                    double userError = distance(data[point], centroids[cluster]);
                    if (userError < minUserError && dataCluster[point] != cluster) {
                        dataCluster[point] = cluster;
                        minUserError = userError;
                        changed = true;
                    }
                }

                totalError += minUserError;
            }

            for (int cluster = 0; cluster < clusterCount; cluster++) {
                int dataClusterCount = 0;
                double[] newCentroid = new double[featuresCount];
                for (int point = 0; point < pointCount; point++) {
                    if (dataCluster[point] == cluster) {
                        dataClusterCount++;
                        for (int i = 0; i < featuresCount; i++) {
                            newCentroid[i] += data[point][i];
                        }
                    }
                }
                pointsInCluster[cluster] = dataClusterCount;
                if (dataClusterCount > 0) {
                    for (int i = 0; i < featuresCount; i++) {
                        centroids[cluster][i] = newCentroid[i] / dataClusterCount;
                    }
                }
            }
            //System.err.println(iteration+"->"+totalError);
            iteration++;
        }
        //System.out.println(clusterCount+";"+totalError);
        this.centroids = centroids;
        this.dataCluster = dataCluster;
        this.error = totalError;
        this.converged = !changed;
    }

    public double[][] getCentroids() {
        return centroids;
    }

    public int[] getDataCluster() {
        return dataCluster;
    }

    public double getError() {
        return error;
    }

    public boolean hasConverged() {
        return converged;
    }

    private double distance(double[] x, double[] y) {
        double dist = 0;
        for(int i = 0; i < featuresCount; i++) {
            dist += (y[i] - x[i])*(y[i] - x[i]);
        }
        return Math.sqrt(dist);
    }


}
