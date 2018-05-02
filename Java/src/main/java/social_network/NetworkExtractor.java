package main.java.social_network;

import main.java.dataset.util.AbstractReader;
import main.java.dataset.util.CallRecord;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class NetworkExtractor extends AbstractReader {

    private int expectedCallsCount;

    public NetworkExtractor(int expectedCallsCount){
        this.expectedCallsCount = expectedCallsCount;
    }

    public Map<CallSocialInfo, Integer> getSocialNetworkInfo(String inputPath) throws Exception{
        Map<CallSocialInfo, Integer> socialInfoCountMap = new HashMap<>(expectedCallsCount);
        this.readInputAndDoStuff(inputPath, new LineProcessor() {
            @Override
            public void processLine(String line) {
                CallSocialInfo info = CallRecord.extractSocialInfo(line);
                socialInfoCountMap.put(info, socialInfoCountMap.getOrDefault(info, 0) + 1);
            }
        });
        return socialInfoCountMap;
    }
}
