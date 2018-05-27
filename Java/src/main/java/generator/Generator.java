package main.java.generator;

import main.java.dataset.intervals.CallIntervals;
import main.java.dataset.util.AbstractReader;
import main.java.dataset.util.CallRecord;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Generator extends AbstractReader {

    public static final String OUTPUT_FILE = "./../cdr_output/%d.csv";
    public static final String CALLERS_LIST = "";
    public static final String PROFILE_TYPES = "";
    public static final String SOCIAL_NETWORK = "";
    public static final String SEP = CallRecord.SEP;

    /**
     *
     * @param args [0] - required parameter, total number of users
     *             [1] - required parameter, simulation day, integer between 1 and 7
     * @throws IOException readers exception
     */
    public static void main(String[] args) throws IOException {

        CallIntervals.initialize();

        String outputFile = String.format(OUTPUT_FILE, System.currentTimeMillis());
        BufferedWriter writer = Files.newBufferedWriter(Paths.get(outputFile));

        int[] userProfiles = new int[Integer.parseInt(args[0])];
        readInputAndDoStuff(CALLERS_LIST, line -> {
            String[] parts = line.split(SEP);
            userProfiles[Integer.parseInt(parts[0])] = Integer.parseInt(parts[1]);
        });

        readInputAndDoStuff(PROFILE_TYPES, line -> {

        });

        readInputAndDoStuff(SOCIAL_NETWORK, line -> {

        });

        for (int hour = 0; hour < 24; hour++) {
            for (int minute = 0; minute < 60; minute++) {
                for (int second = 0; second < 60; second++) {

                }
            }
        }

        flushAndClose(writer);
    }

    public static void writeCallEvent(BufferedWriter writer) throws IOException
    {
        writeln(writer, "");
    }

}
