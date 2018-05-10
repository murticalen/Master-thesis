package main.java.dataset.util;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public abstract class AbstractReader {

    protected static void readInputAndDoStuff(String inputPath, LineProcessor processor) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(inputPath))));
        //skip header
        String line = reader.readLine();
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
        void processLine(String line) throws Exception;
    }

    public List<CallRecord> getAllRecords(String inputPath) throws Exception {
        return getAllRecords(inputPath, (record -> true));
    }

    public List<CallRecord> getAllRecords(String inputPath, Predicate<CallRecord> filter) throws Exception {
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
