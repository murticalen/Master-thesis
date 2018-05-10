package main.java.dataset.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * NOTE: the output file count needs to be smaller than the max available open files for a program (my Mac has 120 limit).
 * There is no explicit regulating of the max count, but maxCallsPerFile parameter should handle it easily.
 */
public class UserCallsSplitter extends AbstractDatasetPreprocessor {

    private int maxCallsPerFile;
    private Map<Integer, Integer> userCallCount;
    private Map<Integer, Integer> userOutputFileMap;

    public UserCallsSplitter(int maxCallsPerFile) {
        this.maxCallsPerFile = maxCallsPerFile;
        this.userCallCount = new LinkedHashMap<>();
        this.userOutputFileMap = new HashMap<>();
    }

    @Override
    public void preProcessDataset(String inputPath, String outputFile) throws Exception {
        readInputAndDoStuff(inputPath, line -> {
            int caller = CallRecord.extractCallerId(line);
            userCallCount.put(caller, userCallCount.getOrDefault(caller, 0) + 1);
        });
        System.out.println(1);
        determineUserFiles();
        System.out.println(2);


        Map<Integer, Writer> fileWriterMap = new HashMap<>();
        readInputAndDoStuff(inputPath, line -> {
            int file = userOutputFileMap.get(CallRecord.extractCallerId(line));
            Writer writer;
            if (!fileWriterMap.containsKey(file)) {
                writer = new BufferedWriter(new FileWriter(outputFile + file + ".csv"));
                fileWriterMap.put(file, writer);
                writeln(writer, CallRecord.HEADER);
            } else {
                writer = fileWriterMap.get(file);
            }
            writeln(writer, line);
        });

        for (Writer writer : fileWriterMap.values()) {
            flushAndClose(writer);
        }
    }

    private void determineUserFiles() {
        int i = 0;
        int actualMaxCalls = (int) (1.3 * maxCallsPerFile);
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
