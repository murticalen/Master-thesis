package main.java.dataset.util;

import main.java.dataset.intervals.CallIntervals;

import java.io.*;
import java.util.*;

/**
 * NOTE: the output file count needs to be smaller than the max available open files for a program (my Mac has 120 limit)
 */
public class UserCallsSplitter extends AbstractDatasetPreprocessor {

    private int maxCallsPerFile;
    private Map<String, Integer> userCallCount;
    private Map<String, Integer> userOutputFileMap;

    public UserCallsSplitter(int maxCallsPerFile) {
        this.maxCallsPerFile = maxCallsPerFile;
        this.userCallCount = new LinkedHashMap<>();
        this.userOutputFileMap = new HashMap<>();
    }

    @Override
    public void preProcessDataset(String inputPath, String outputFile) throws Exception {
        this.readInputAndDoStuff(inputPath, new LineProcessor() {
            @Override
            public void processLine(String line) {
                String caller = CallRecord.extractCallerId(line);
                userCallCount.put(caller, userCallCount.getOrDefault(caller, 0) + 1);
            }
        });
        System.out.println(1);
        determineUserFiles();
        System.out.println(2);


        Map<Integer, Writer> filesFound = new HashMap<>();
        this.readInputAndDoStuff(inputPath, new LineProcessor() {
            @Override
            public void processLine(String line) throws Exception{
                CallRecord record = CallRecord.read(line);
                int file = userOutputFileMap.get(record.getCallerId());
                Writer writer;
                if (!filesFound.containsKey(file)) {
                    writer = new BufferedWriter(new FileWriter(outputFile + file + ".csv"));
                    filesFound.put(file, writer);
                    writeln(writer, CallRecord.HEADER+CallRecord.SEP+CallIntervals.INTERVAL_HEADER);
                } else {
                    writer = filesFound.get(file);
                }
                writeln(writer, record.toString()+CallRecord.SEP+CallIntervals.extractIntervals(record));
            }
        });

        for (Writer writer : filesFound.values()) {
            writer.flush();
            writer.close();
        }
    }

    private void determineUserFiles() {
        int i = 0;
        int actualMaxCalls = (int)(1.3 * maxCallsPerFile);
        Map<Integer, Integer> availableFiles = new HashMap<>();
        for (var userCount : userCallCount.entrySet()) {
            if (availableFiles.isEmpty()) {
                availableFiles.put(i, 0);
                i++;
            }
            int picked = i + 1;
            for (var item : availableFiles.entrySet()) {
                if (item.getValue() + userCount.getValue() <= actualMaxCalls) {
                    picked = item.getKey();
                    break;
                }
            }
            userOutputFileMap.put(userCount.getKey(), picked);
            availableFiles.put(picked, availableFiles.getOrDefault(picked, 0) + userCount.getValue());
            if (availableFiles.get(picked) > maxCallsPerFile) {
                availableFiles.remove(picked);
            }
        }
        userCallCount.clear();
    }
}
