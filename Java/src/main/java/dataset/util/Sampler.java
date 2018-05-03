package main.java.dataset.util;

import jdk.jshell.spi.ExecutionControl;

import java.util.List;
import java.util.function.Predicate;

public class Sampler extends AbstractReader {

    private final double sampleOdds;
    private final int sampleSize;
    private final int totalSize;

    public Sampler(int sampleSize, int totalSize) {
        this.sampleSize = sampleSize;
        this.totalSize = totalSize;
        this.sampleOdds = sampleSize * 1.0 / totalSize;
    }

    @Override
    public List<CallRecord> getAllRecords(String inputPath) throws Exception {
        return super.getAllRecords(inputPath, record -> Math.random() < sampleOdds);
    }

    @Override
    public List<CallRecord> getAllRecords(String inputPath, Predicate<CallRecord> filter) throws Exception {
        return super.getAllRecords(inputPath, record -> Math.random() < sampleOdds && filter.test(record));
    }
}
