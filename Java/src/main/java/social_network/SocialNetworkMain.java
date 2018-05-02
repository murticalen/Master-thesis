package main.java.social_network;

import main.java.dataset.DatasetMain;

import java.util.Map;

public class SocialNetworkMain {

    public static final int CALLS_COUNT = DatasetMain.TOTAL_SIZE;

    public static void main(String[] args) throws Exception{

        NetworkExtractor networkExtractor = new NetworkExtractor(CALLS_COUNT);
        var networkInfo = networkExtractor.getSocialNetworkInfo(DatasetMain.OUTPUT_FILE);

        System.out.println(Runtime.getRuntime().totalMemory());
        System.out.println(Runtime.getRuntime().maxMemory());
        for (var entry : networkInfo.entrySet()) {
            if (entry.getValue() > 10) {
                System.out.println(entry);
            }
        }

    }

}
