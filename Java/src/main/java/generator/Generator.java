package main.java.generator;

import main.java.dataset.util.AbstractReader;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Generator extends AbstractReader {

    public static final String OUTPUT_FILE = "./../cdr_output/%d.csv";

    public static void main(String[] args) throws IOException {

        String outputFile = String.format(OUTPUT_FILE, System.currentTimeMillis());

        System.out.println(outputFile);

        BufferedWriter writer = Files.newBufferedWriter(Paths.get(outputFile));

        double sum = Math.random();
        int total = 1;
        int cmp = (int)10e6;

        while (Math.round(sum / total * cmp) * 1.0 / cmp != 0.5) {
            double random = Math.random();
            sum += random;
            total++;
            writeln(writer, String.valueOf(random));
        }

        System.out.println(total);

        flushAndClose(writer);
    }

}
