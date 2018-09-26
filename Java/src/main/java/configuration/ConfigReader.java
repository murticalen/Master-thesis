package main.java.configuration;

import main.java.dataset.intervals.CallIntervals;
import main.java.dataset.util.AbstractReader;

import java.io.IOException;

public class ConfigReader extends AbstractReader {

    private static String configFile;

    public static void readConfig() throws IOException {

        CallIntervals.initialize();

        readInputAndDoStuffNoSkip(Constants.INPUT_CONFIG_LOCATION, line -> {
            configFile = line;
        });

        readInputAndDoStuff(configFile, line -> {
            String[] parts = line.split(Constants.SEPARATOR);
            switch (parts[0]) {
                case "user-count":
                    Constants.USER_COUNT = Integer.parseInt(parts[1]);
                    break;
                case "interval-count":
            }
        });
    }

}
