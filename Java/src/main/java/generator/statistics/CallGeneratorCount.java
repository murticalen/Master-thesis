package main.java.generator.statistics;

import main.java.configuration.Constants;
import main.java.dataset.util.AbstractReader;
import main.java.generator.GeneratorHelper;
import main.java.generator.duration.QuantileDuration;

import java.io.IOException;
import java.io.Writer;

public class CallGeneratorCount extends AbstractReader {

    public static final String SEP = Constants.SEPARATOR;
    public static final String EXPECTED_CALL_COUNT_OUTPUT = "./../statistics/expected_call_count.csv";

    public void callCountExamples(int testExamples, int testCount, Writer outputWriter) throws IOException {
        double[] expectedCalls = new double[]{0.01, 0.05, 0.1, 0.2, 0.4, 0.6, 0.8, 1, 2, 4, 8, 20, 100, 1000, 10000, 100000};
        writeln(outputWriter, "expectedValue" + SEP + "expectedTotalCalls" + SEP + "generatedCalls");
        for (double expectedCallCount : expectedCalls) {
            for (int testOrder = 0; testOrder < testCount; testOrder++) {
                long totalExpectedCount = (long) (expectedCallCount * testExamples);
                long actualGeneratedCount = 0;
                for (int i = 0; i < testExamples; i++) {
                    long cnt = GeneratorHelper.calculateExpectedCalls(expectedCallCount);
                    actualGeneratedCount += cnt;
                }
                writeln(outputWriter, expectedCallCount + SEP + totalExpectedCount + SEP + actualGeneratedCount);
            }
        }
        flushAndClose(outputWriter);
    }

    public void callDurationPercentile() {
        for (int i = 0; i < 10000000; i++) {
            System.out.println(QuantileDuration.getRandomQuantilePosition());
        }
    }

    public static void main(String[] args) throws IOException {
        CallGeneratorCount callGeneratorCount = new CallGeneratorCount();
        //callGeneratorCount.callCountExamples(50000, 1000, Files.newBufferedWriter(Paths.get(EXPECTED_CALL_COUNT_OUTPUT)));
        callGeneratorCount.callDurationPercentile();
        System.out.println(QuantileDuration.map);
    }

}
