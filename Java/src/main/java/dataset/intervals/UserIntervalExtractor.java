package main.java.dataset.intervals;

import main.java.dataset.model.CallRecord;
import main.java.dataset.util.AbstractReader;
import main.java.configuration.Constants;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class UserIntervalExtractor extends AbstractReader {

    public UserIntervalExtractor() {
        CallIntervals.initialize();
    }

    /**
     * A method which calculates the probability of user making a call in each interval of each day.
     *
     * @param userSplitInput the folder which contains n csv files split by {@link main.java.dataset.util.UserCallsSplitter}
     * @param outputFile     file which will contain the csv result of this method
     * @throws IOException
     */
    public void extractAndSaveIntervals(String userSplitInput, String outputFile, String dateOutputFile) throws IOException {
        List<String> files = Files.list(Paths.get(userSplitInput)).map(Path::toString).collect(Collectors.toList());

        BufferedWriter userIntervalsWriter = new BufferedWriter(new FileWriter(outputFile));
        writeln(userIntervalsWriter, CallIntervals.MULTI_LINE_INTERVAL_HEADER);

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
            for (Map.Entry<Integer, List<CallRecord>> entry : userIdCallsMap.entrySet()) {
                List<Map<Integer, Map<String, Double>>> userIntervalList = entry.getValue()
                        .stream()
                        .map(IntervalHelper::extractIntervals)
                        //ignore days with no features to stores less data on disk
                        .filter(map -> !map.isEmpty())
                        .collect(Collectors.toList());

                Map<Integer, Map<String, Double>> totalUserIntervals = joinUserIntervals(userIntervalList);
                writeln(userIntervalsWriter, CallIntervals.getMultiLineIntervalsString(totalUserIntervals, entry.getKey()));
            }
        }

        flushAndClose(userIntervalsWriter);

        Writer dateOutputWriter = Files.newBufferedWriter(Paths.get(dateOutputFile));
        writeln(dateOutputWriter, "weekday" + Constants.SEPARATOR + "date");
        for (Map.Entry<Integer, Set<String>> weekdayDays : CallIntervals.weekDayDateMap.entrySet()) {
            for (String date : weekdayDays.getValue()) {
                writeln(dateOutputWriter, weekdayDays.getKey() + Constants.SEPARATOR + date);
            }
        }
        flushAndClose(dateOutputWriter);
    }

    /**
     * A methods that calculates sum of the probabilities of calls made by the user in the days and intervals of his calls
     *
     * @param list All calculated interval values for each user calls
     * @return sum of probabilities split by day and interval
     */
    private static Map<Integer, Map<String, Double>> joinUserIntervals(List<Map<Integer, Map<String, Double>>> list) {
        Map<Integer, Map<String, Double>> result = new HashMap<>();
        for (Map<Integer, Map<String, Double>> callIntervals : list) {
            for (Map.Entry<Integer, Map<String, Double>> intervalEntry : callIntervals.entrySet()) {
                Map<String, Double> dayIntervals = result.getOrDefault(intervalEntry.getKey(), new HashMap<>());
                for (Map.Entry<String, Double> intervalValue : intervalEntry.getValue().entrySet()) {
                    dayIntervals.put(intervalValue.getKey(), dayIntervals.getOrDefault(intervalValue.getKey(), 0.0) + intervalValue.getValue());
                }
                result.put(intervalEntry.getKey(), dayIntervals);
            }

        }

        for (Map.Entry<Integer, Map<String, Double>> dayIntervalsIntensity : result.entrySet()) {
            for (Map.Entry<String, Double> intervalIntensity : dayIntervalsIntensity.getValue().entrySet()) {
                intervalIntensity.setValue(intervalIntensity.getValue() / 13);
            }
        }

        return result;
    }

}
