package main.java.dataset.util;

import main.java.dataset.model.CallRecord;

import java.io.IOException;
import java.util.List;
import java.util.function.Predicate;

public class Sampler extends AbstractReader {
    
    private       double sampleOdds;
    private final int    sampleSize;
    private final int    totalSize;
    
    public Sampler(int sampleSize, int totalSize) {
        this.sampleSize = sampleSize;
        this.totalSize = totalSize;
        this.sampleOdds = sampleSize * 1.0 / totalSize;
    }
    
    @Override
    public List<CallRecord> getAllRecords(String inputPath) throws IOException {
        return super.getAllRecords(inputPath, record -> Math.random() < sampleOdds);
    }
    
    @Override
    public List<CallRecord> getAllRecords(String inputPath, Predicate<CallRecord> filter) throws IOException {
        List<CallRecord> records = super.getAllRecords(inputPath, filter);
        sampleOdds = sampleSize * 1.0 / records.size();
        records.removeIf(record -> !(Math.random() < sampleOdds));
        return records;
    }
}
