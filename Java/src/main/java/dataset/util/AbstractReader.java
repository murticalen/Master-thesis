package main.java.dataset.util;

import main.java.dataset.model.CallRecord;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public abstract class AbstractReader {

    protected static void readInputAndDoStuff(String inputPath, LineProcessor processor) throws IOException {
        readInputAndDoStuff(inputPath, processor, true);
    }

    protected static void readInputAndDoStuffNoSkip(String inputPath, LineProcessor processor) throws IOException {
        readInputAndDoStuff(inputPath, processor, false);
    }

    private static void readInputAndDoStuff(String inputPath, LineProcessor processor, boolean skipHeader) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(inputPath)));
        //skip header
        String line;
        if (skipHeader) {
            line = reader.readLine();
        }
        while ((line = reader.readLine()) != null) {
            processor.processLine(line);
        }
        reader.close();
    }

    protected static void writeln(Writer writer, String line) throws IOException {
        writer.write(line + System.lineSeparator());
    }

    protected static void flushAndClose(Writer writer) throws IOException {
        writer.flush();
        writer.close();
    }

    protected interface LineProcessor {
        void processLine(String line) throws IOException;
    }

    public List<CallRecord> getAllRecords(String inputPath) throws IOException {
        return getAllRecords(inputPath, (record -> true));
    }

    public List<CallRecord> getAllRecords(String inputPath, Predicate<CallRecord> filter) throws IOException {
        List<CallRecord> recordList = new ArrayList<>();
        readInputAndDoStuff(inputPath, line -> {
            CallRecord record = CallRecord.read(line);
            if (filter.test(record)) {
                recordList.add(CallRecord.read(line));
            }
        });
        return recordList;
    }
}
