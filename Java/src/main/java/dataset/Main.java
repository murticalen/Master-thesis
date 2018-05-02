package main.java.dataset;

import main.java.dataset.intervals.CallIntervals;
import main.java.dataset.util.AbstractDatasetPreprocessor;
import main.java.dataset.util.CallRecord;
import main.java.dataset.util.MulticomLandlineDatasetPreprocessor;
import main.java.dataset.util.Sampler;

import java.util.List;

public class Main {

    private static final String MULTICOM_LANDLINE_INPUT = "./../dataset/";
    private static final String OUTPUT_FILE = "./../dataset/combined.csv";
    //sample 100k calls out of 24 mil.
    private static final int SAMPLE_SIZE = 100000;
    private static final int TOTAL_SIZE = 23712942;

    public static void main(String[] args) throws Exception {
        CallIntervals.initializeHeader();

        AbstractDatasetPreprocessor preprocessor = new MulticomLandlineDatasetPreprocessor();
        preprocessor.preProcessDataset(MULTICOM_LANDLINE_INPUT, OUTPUT_FILE);

        Sampler sampler = new Sampler(SAMPLE_SIZE, TOTAL_SIZE);
        //List<CallRecord> samples = sampler.readDataset(OUTPUT_FILE);

        System.out.println(CallIntervals.INTERVAL_HEADER);
    }
}
