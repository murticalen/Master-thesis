package main.java.dataset.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Sampler {

    private final double sampleOdds;
    private final int sampleSize;
    private final int totalSize;

    public Sampler(int sampleSize, int totalSize) {
        this.sampleSize = sampleSize;
        this.totalSize = totalSize;
        this.sampleOdds = sampleSize * 1.0 / totalSize;
    }

    public List<CallRecord> readDataset(String inputPath) throws Exception {
        List<CallRecord> records = new ArrayList<>(sampleSize);
        var reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(inputPath))));
        //ignore header
        String line = reader.readLine();

        while ((line = reader.readLine()) != null) {
            if (isIncluded()) {
                records.add(CallRecord.read(line));
            }

        }
        reader.close();

        return records;
    }

    protected boolean isIncluded() {
        return Math.random() < sampleOdds;
    }
}
