package main.java.dataset.intervals;

import main.java.dataset.util.AbstractReader;
import main.java.dataset.util.CallRecord;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class UserIntervalExtractor extends AbstractReader {

    public UserIntervalExtractor() {
        CallIntervals.initialize();
    }

    public void extractAndSaveIntervals(String userSplittedInput, String outputFile) throws Exception {
        List<String> files = Files.list(Paths.get(userSplittedInput)).map(Path::toString).collect(Collectors.toList());

        BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
        writeln(writer, "userId" + CallIntervals.SEP + CallIntervals.INTERVAL_HEADER);

        for (String file : files) {
            System.out.println(file);
            Map<Integer, List<CallRecord>> userIdCallsMap = new HashMap<>();
            readInputAndDoStuff(file, line -> {
                CallRecord record = CallRecord.read(line);
                List<CallRecord> records = userIdCallsMap.getOrDefault(record.getCallerId(), new ArrayList<>());
                records.add(record);
                userIdCallsMap.put(record.getCallerId(), records);
            });
            for (var entry : userIdCallsMap.entrySet()) {
                var userIntervalList = entry.getValue()
                        .stream()
                        .map(IntervalHelper::extractIntervals)
                        .collect(Collectors.toList());
                var averagedUserIntervals = joinUserIntervals(userIntervalList);
                writeln(writer, entry.getKey()+CallIntervals.SEP+CallIntervals.getIntervalsString(averagedUserIntervals));
            }
        }

        flushAndClose(writer);
    }

    private static Map<Integer, Map<String, Double>> joinUserIntervals(List<Map<Integer, Map<String, Double>>> list) {
        Map<Integer, Map<String, Double>> result = new HashMap<>();
        for (var callIntervals : list) {
            for (Map.Entry<Integer, Map<String, Double>> intervalEntry : callIntervals.entrySet()) {
                var dayIntervals = result.getOrDefault(intervalEntry.getKey(), new HashMap<>());
                for (var intervalValue : intervalEntry.getValue().entrySet()) {
                    dayIntervals.put(intervalValue.getKey(), dayIntervals.getOrDefault(intervalValue.getKey(), 0.0) + intervalValue.getValue());
                }
                result.put(intervalEntry.getKey(), dayIntervals);
            }
        }
        int n = list.size();
        for (int day : CallIntervals.DAYS) {
            if (result.containsKey(day)) {
                var entry = result.get(day);
                for (String interval : CallIntervals.INTERVALS) {
                    entry.put(interval, entry.get(interval) / n);
                }
            }
        }
        return result;
    }

}
