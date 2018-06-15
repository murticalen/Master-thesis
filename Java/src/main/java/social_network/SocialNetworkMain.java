package main.java.social_network;

import main.java.dataset.DatasetMain;
import main.java.dataset.intervals.CallIntervals;
import main.java.dataset.model.CallRecord;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class SocialNetworkMain {

    public static final int CALLS_COUNT = DatasetMain.TOTAL_SIZE;
    public static final String INPUT_FILE = DatasetMain.OUTPUT_FILE;
    public static final String FILTERED_OUTPUT = "./../dataset/20plus_calls.csv";

    public static void main(String[] args) throws IOException {
        CallIntervals.initialize();


        NetworkExtractor networkExtractor = new NetworkExtractor(CALLS_COUNT, false, 20);
        Map<CallSocialInfo, Integer> networkInfo = networkExtractor.getSocialNetworkInfo(INPUT_FILE, FILTERED_OUTPUT);

        List<CallRecord> records = networkExtractor.getAllRecords(FILTERED_OUTPUT, record -> record.getCallerId() == 1);
        records.sort((r1, r2) -> {
            return r1.getCallTime().compareTo(r2.getCallTime());
        });
    }

}
