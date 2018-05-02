package main.java.dataset.util;

import java.io.*;

public abstract class AbstractReader {

    protected void readInputAndDoStuff(String inputPath, LineProcessor processor) throws Exception{
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(inputPath))));
        //skip header
        String line = reader.readLine();
        while ((line = reader.readLine()) != null) {
            processor.processLine(line);
        }
        reader.close();
    }

    protected void writeln(Writer writer, String line) throws IOException
    {
        writer.write(line + System.lineSeparator());
    }

    protected interface LineProcessor{
        void processLine(String line) throws Exception;
    }

}
