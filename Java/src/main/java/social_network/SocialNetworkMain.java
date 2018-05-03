package main.java.social_network;

import main.java.dataset.DatasetMain;
import main.java.dataset.util.CallRecord;

import java.util.*;

public class SocialNetworkMain {

    public static final int CALLS_COUNT = DatasetMain.TOTAL_SIZE;
    public static final String INPUT_FILE = DatasetMain.OUTPUT_FILE;
    public static final String FILTERED_OUTPUT = "./../dataset/20plus_calls.csv";

    public static void main(String[] args) throws Exception {
        NetworkExtractor networkExtractor = new NetworkExtractor(CALLS_COUNT, false, 20);
        var networkInfo = networkExtractor.getSocialNetworkInfo(INPUT_FILE, FILTERED_OUTPUT);

        List<CallRecord> records = networkExtractor.getAllRecords(FILTERED_OUTPUT, record -> record.getCallerId() == 1);
        records.sort((r1, r2) -> {
            return r1.getCallTime().compareTo(r2.getCallTime());
        });
        records.forEach(System.out::println);
        System.out.println(records.size());
    }

}
