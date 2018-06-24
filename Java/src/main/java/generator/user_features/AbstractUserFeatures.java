package main.java.generator.user_features;

import main.java.dataset.util.AbstractReader;
import main.java.configuration.Constants;
import main.java.generator.GeneratorHelper;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;

public abstract class AbstractUserFeatures extends AbstractReader {

    protected String SEP = Constants.SEPARATOR;
    private static double totalExpectedCalls;
    private static long generatedExpectedCalls;
    protected Writer expectedCallsWriter;
    protected int userCount;

    public AbstractUserFeatures(int userCount) {
        this.userCount = userCount;
    }

    public abstract long[][] determineUserCallCount(int day, String expectedCallsFile) throws IOException;

    public abstract void readData() throws IOException;

    public abstract void clearFeatures();

    public static long generateCallCount(double expectedCalls) {
        totalExpectedCalls += expectedCalls;
        long n = GeneratorHelper.calculateExpectedCalls(expectedCalls);
        generatedExpectedCalls += n;
        return n;
    }

    public double getTotalExpectedCalls() {
        return totalExpectedCalls;
    }

    public long getGeneratedExpectedCalls() {
        return generatedExpectedCalls;
    }

    protected void writeHeader() throws IOException {
        writeln(expectedCallsWriter, "user" + SEP + "interval" + SEP + "cnt");
    }

    protected long getAndWriteExpectedCallCount(int user, int interval, double expectedCalls) throws IOException {
        long n = generateCallCount(expectedCalls);
        writeln(expectedCallsWriter, user + SEP + interval + SEP + n);
        return n;
    }

    public long[][] generateExpectedCounts(String expectedCallsFile, IExpectedCallsGetter expectedCallGetter) throws IOException {
        expectedCallsWriter = Files.newBufferedWriter(Paths.get(expectedCallsFile));

        long[][] userIntervalCallCount = new long[userCount][Constants.INTERVAL_COUNT];
        writeHeader();
        for (int user = 0; user < userIntervalCallCount.length; user++) {
            for (int interval = 0; interval < Constants.INTERVAL_COUNT; interval++) {
                userIntervalCallCount[user][interval] = getAndWriteExpectedCallCount(user, interval, expectedCallGetter.getExpectedCalls(user, interval));
            }
        }
        flushAndClose(expectedCallsWriter);
        return userIntervalCallCount;
    }

}
