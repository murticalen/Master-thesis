package main.java.dataset.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class KMeansWriter extends AbstractReader {
    
    private KMeans kMeans;
    private String separator;

    public KMeansWriter(KMeans kMeans, String separator) {
        this.kMeans = kMeans;
        this.separator = separator;
    }
    
    public void setKMeans(KMeans kMeans) {
        this.kMeans = kMeans;
    };

    public void setSeparator(String separator) {
        this.separator = separator;
    }
    
    public void writeDataCluster(String file) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        int[] dataCluster = kMeans.getDataCluster();
        for (int i = 0, size = dataCluster.length; i < size; i++) {
            writeln(writer, i + separator + dataCluster[i]);
        }
        flushAndClose(writer);
    }

    public void writeCentroids(String file, double[] featureMaxes) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        double[][] centroids = kMeans.getCentroids();
        for (int centroid = 0; centroid < centroids.length; centroid++) {
            StringBuilder sb = new StringBuilder();
            sb.append(centroid);
            for (int feature = 0; feature < centroids[centroid].length; feature++) {
                sb.append(separator);
                sb.append(centroids[centroid][feature] * featureMaxes[feature]);
            }
            writeln(writer, sb.toString());
        }
        flushAndClose(writer);
    }
    
}
