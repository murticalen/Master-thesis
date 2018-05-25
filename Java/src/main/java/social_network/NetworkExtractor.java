package main.java.social_network;

import main.java.dataset.util.AbstractReader;
import main.java.dataset.util.CallRecord;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;

public class NetworkExtractor extends AbstractReader {

    private int expectedCallsCount;
    private int minimumCallsFilter;

    public NetworkExtractor(int expectedCallsCount, boolean directed) {
        this(expectedCallsCount, directed, 0);
    }

    public NetworkExtractor(int expectedCallsCount, boolean directed, int minimumCallsFilter) {
        this.expectedCallsCount = expectedCallsCount;
        CallSocialInfo.DIRECTED = directed;
        this.minimumCallsFilter = minimumCallsFilter;
    }

    public Map<CallSocialInfo, Integer> getSocialNetworkInfo(String inputPath) throws Exception {
        return getSocialNetworkInfo(inputPath, null);
    }

    public Map<CallSocialInfo, Integer> getSocialNetworkInfo(String inputPath, String outputPath) throws Exception {
        Map<CallSocialInfo, Integer> socialInfoCountMap = new HashMap<>(expectedCallsCount);
        readInputAndDoStuff(inputPath, line -> {
            CallSocialInfo info = CallRecord.extractSocialInfo(line);
            socialInfoCountMap.put(info, socialInfoCountMap.getOrDefault(info, 0) + 1);
        });
        //remove nodes between users who haven't called each other more than m
        if (minimumCallsFilter > 0) {
            socialInfoCountMap.entrySet().removeIf(entry -> entry.getValue() < minimumCallsFilter);
        }
        if (outputPath != null) {
            BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath));
            writeln(writer, CallRecord.HEADER);
            readInputAndDoStuff(inputPath, line -> {
                CallSocialInfo info = CallRecord.extractSocialInfo(line);
                if (socialInfoCountMap.containsKey(info)) {
                    writeln(writer, CallRecord.read(line).toString());
                }
            });
            flushAndClose(writer);
        }

        return socialInfoCountMap;
    }

    public Map<Integer, Map<Integer, Integer>> getSocialNetworkInfoV2(String inputPath) throws Exception {
        Map<Integer, Map<Integer, Integer>> socialInfoCountMap = new HashMap<>(expectedCallsCount);
        readInputAndDoStuff(inputPath, line -> {
            CallSocialInfo info = CallRecord.extractSocialInfo(line);
            Map<Integer, Integer> otherMap = socialInfoCountMap.getOrDefault(info.getCallerId(), new HashMap<>());
            otherMap.put(info.getReceiverId(), otherMap.getOrDefault(info.getReceiverId(), 0) + 1);
            socialInfoCountMap.put(info.getCallerId(), otherMap);
        });
        return socialInfoCountMap;
    }
}
