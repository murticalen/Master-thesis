package main.java.generator.statistics;

import main.java.dataset.model.CallRecord;
import main.java.dataset.util.AbstractReader;
import main.java.dataset.util.Constants;
import main.java.generator.GeneratorHelper;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;

public class CallGeneratorCount extends AbstractReader {

    public static final String SEP = Constants.SEPARATOR;
    public static final String EXPECTED_CALL_COUNT_OUTPUT = "./../statistics/expected_call_count.csv";

    public void callCountExamples(int testExamples, int testCount, Writer outputWriter) throws IOException {
        double[] expectedCalls = new double[]{0.1, 0.2, 0.4, 0.6, 0.8, 1, 1.5, 2, 3, 4, 5, 8, 20, 40, 80, 160, 300, 500, 1000, 10000};
        writeln(outputWriter, "expectedValue" + SEP + "expectedTotalCalls" + SEP + "generatedCalls");
        for (double expectedCallCount : expectedCalls) {
            for (int testOrder = 0; testOrder < testCount; testOrder++) {
                long totalExpectedCount = (long) (expectedCallCount * testExamples);
                long actualGeneratedCount = 0;
                for (int i = 0; i < testExamples; i++) {
                    long cnt = GeneratorHelper.uniformCallCountRandom(expectedCallCount);
                    actualGeneratedCount += cnt;
                }
                writeln(outputWriter, expectedCallCount + SEP + totalExpectedCount + SEP + actualGeneratedCount);
            }
        }
        flushAndClose(outputWriter);
    }

    public static void main(String[] args) throws IOException {
        CallGeneratorCount callGeneratorCount = new CallGeneratorCount();
        callGeneratorCount.callCountExamples(50000, 1000, Files.newBufferedWriter(Paths.get(EXPECTED_CALL_COUNT_OUTPUT)));
    }

}
