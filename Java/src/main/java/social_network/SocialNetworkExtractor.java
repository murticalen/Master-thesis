package main.java.social_network;

import main.java.dataset.DatasetMain;
import main.java.dataset.util.AbstractReader;
import main.java.configuration.Constants;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class SocialNetworkExtractor extends AbstractReader {

    public static final String SEP = Constants.SEPARATOR;
    public static final String OUTPUT_PATH = "./../dataset/social_network.csv";
    private boolean directed;
    private Map<Integer, Map<Integer, Integer>> callersConnections = new HashMap<>();
    private Map<Integer, Integer> callerTotalCallsCount = new HashMap<>();

    public SocialNetworkExtractor(boolean directed) {
        this.directed = directed;
    }

    public void run(String INPUT_PATH, String OUTPUT_PATH) throws IOException {

        readInputAndDoStuff(INPUT_PATH, line -> {
            String[] parts = line.split(SEP);
            int caller = Integer.parseInt(parts[1]);
            int receiver = Integer.parseInt(parts[2]);
            if (!directed) {
                if (receiver < caller) {
                    int temp = caller;
                    caller = receiver;
                    receiver = temp;
                }
                //storeCallData(receiver, caller);
                callerTotalCallsCount.put(receiver, callerTotalCallsCount.getOrDefault(receiver, 0) + 1);
            }
            storeCallData(caller, receiver);
        });

        BufferedWriter writer = Files.newBufferedWriter(Paths.get(OUTPUT_PATH));
        for (Map.Entry<Integer, Map<Integer, Integer>> callerReceiverCount : callersConnections.entrySet()) {
            int caller = callerReceiverCount.getKey();
            if (caller == 1064785) {
                System.out.println(callerReceiverCount.getValue().entrySet());
            }
            for (Map.Entry<Integer, Integer> receiverCallCount : callerReceiverCount.getValue().entrySet()) {
                writeConnectionData(writer, caller, receiverCallCount.getKey(), receiverCallCount.getValue());
                if (!directed) {
                    writeConnectionData(writer, receiverCallCount.getKey(), caller, receiverCallCount.getValue());
                }
            }
        }
        flushAndClose(writer);
    }

    private void storeCallData(int caller, int receiver) {
        callerTotalCallsCount.put(caller, callerTotalCallsCount.getOrDefault(caller, 0) + 1);
        Map<Integer, Integer> receiverCount = callersConnections.getOrDefault(caller, new HashMap<>());
        receiverCount.put(receiver, receiverCount.getOrDefault(receiver, 0) + 1);
        callersConnections.put(caller, receiverCount);
    }

    private void writeConnectionData(BufferedWriter writer, int caller, int receiver, int connectionCalLCount) throws IOException {
        double connection = connectionCalLCount * 1.0 / callerTotalCallsCount.get(caller);
        writeln(writer, caller + SEP + receiver + SEP + connection);
    }

    public static void main(String[] args) throws IOException {
        SocialNetworkExtractor socialNetworkExtractor = new SocialNetworkExtractor(true);
        socialNetworkExtractor.run(DatasetMain.OUTPUT_FILE, OUTPUT_PATH);
    }

}
