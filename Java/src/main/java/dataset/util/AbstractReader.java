package main.java.dataset.util;

import java.io.*;

public abstract class AbstractReader {

    protected void readInputAndDoStuff(String inputPath, LineProcessor processor) throws Exception{
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(inputPath))));
        //skip header
        String line = reader.readLine();
        while ((line = reader.readLine()) != null) {
            CallRecord record = CallRecord.read(line);
            String callerId = record.getCallerId();
            processor.processLine(line);
        }
        reader.close();
    }

    protected void writeln(Writer writer, String line) throws IOException
    {
        writer.write(line);
        writer.write(System.lineSeparator());
    }

    protected static abstract class LineProcessor{
        public abstract void processLine(String line) throws Exception;
    }

}
