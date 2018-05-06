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

    /**
     * A method which calculates the probability of user making a call in each interval of each day.
     * @param userSplitInput the folder which contains n csv files split by {@link main.java.dataset.util.UserCallsSplitter}
     * @param outputFile file which will contain the csv result of this method
     * @throws Exception IO or Parse exceptions
     */
    public void extractAndSaveIntervals(String userSplitInput, String outputFile) throws Exception {
        List<String> files = Files.list(Paths.get(userSplitInput)).map(Path::toString).collect(Collectors.toList());

        BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
        writeln(writer, CallIntervals.MULTI_LINE_INTERVAL_HEADER);

        for (String file : files) {
            System.out.println(file);
            Map<Integer, List<CallRecord>> userIdCallsMap = new HashMap<>();
            //read all calls in a file, group them by user
            readInputAndDoStuff(file, line -> {
                CallRecord record = CallRecord.read(line);
                List<CallRecord> records = userIdCallsMap.getOrDefault(record.getCallerId(), new ArrayList<>());
                records.add(record);
                userIdCallsMap.put(record.getCallerId(), records);
            });
            //for each user, go through all his calls and group the calculated interval probabilities
            for (var entry : userIdCallsMap.entrySet()) {
                var userIntervalList = entry.getValue()
                        .stream()
                        .map(IntervalHelper::extractIntervals)
                        //ignore illegal days (e.g. we don't want Sunday New Year's Day to mess up regular Sunday
                        .filter(map -> !map.isEmpty())
                        .collect(Collectors.toList());

                var totalUserIntervals = joinUserIntervals(userIntervalList);
                writeln(writer, CallIntervals.getMultiLineIntervalsString(totalUserIntervals, entry.getKey()));
            }
        }

        flushAndClose(writer);
    }

    /**
     * A methods that calculates sum of the probabilities of calls made by the user in the days and intervals of his calls
     * @param list All calculated interval values for each user calls
     * @return sum of probabilities split by day and interval
     */
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
        return result;
    }

}
