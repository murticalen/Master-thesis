package main.java.dataset.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class KMeansWriter extends AbstractReader {
    
    private KMeans kMeans;
    private String separator;
    private String dataName;
    
    public KMeansWriter(KMeans kMeans, String separator) {
        this(kMeans, separator, "data");
    }
    
    public KMeansWriter(KMeans kMeans, String separator, String dataName) {
        this.kMeans = kMeans;
        this.separator = separator;
        this.dataName = dataName;
    }
    
    public void setKMeans(KMeans kMeans) {
        this.kMeans = kMeans;
    }
    
    ;
    
    public void setSeparator(String separator) {
        this.separator = separator;
    }
    
    public void writeDataCluster(String file) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        writeln(writer, dataName + separator + "cluster");
        int[] dataCluster = kMeans.getDataCluster();
        for (int i = 0, size = dataCluster.length; i < size; i++) {
            writeln(writer, i + separator + dataCluster[i]);
        }
        flushAndClose(writer);
    }
    
    public void writeCentroids(String file, double[] featureMaxes) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        writeln(writer, "cluster" + separator + "feature" + separator + "value");
        double[][] centroids = kMeans.getCentroids();
        for (int centroid = 0; centroid < centroids.length; centroid++) {
            //StringBuilder sb = new StringBuilder();
            //sb.append(centroid);
            for (int feature = 0; feature < centroids[centroid].length; feature++) {
                //sb.append(separator);
                //sb.append(centroids[centroid][feature] * featureMaxes[feature]);
                writeln(writer, centroid + separator + feature + separator + centroids[centroid][feature] * featureMaxes[feature]);
            }
            //writeln(writer, sb.toString());
        }
        flushAndClose(writer);
    }
    
}
