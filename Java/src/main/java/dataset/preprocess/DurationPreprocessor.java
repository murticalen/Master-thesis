package main.java.dataset.preprocess;

import main.java.configuration.Constants;
import main.java.dataset.model.CallRecord;
import main.java.dataset.util.AbstractReader;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;

public class DurationPreprocessor extends AbstractReader {
    
    private IDurationWriter durationWriter;
    
    public DurationPreprocessor() {
        this(true, true);
    }
    
    public DurationPreprocessor(boolean callerIncluded, boolean receiverIncluded) {
        if (!callerIncluded && !receiverIncluded) {
            throw new IllegalArgumentException();
        }
        if (callerIncluded && receiverIncluded) {
            durationWriter = ((outputWriter, record) -> {
                DurationPreprocessor.writeDuration(outputWriter, record.getCallerId(), record.getDuration());
                DurationPreprocessor.writeDuration(outputWriter, record.getReceiverId(), record.getDuration());
            });
        } else if (callerIncluded) {
            durationWriter = ((outputWriter, record) -> {
                DurationPreprocessor.writeDuration(outputWriter, record.getCallerId(), record.getDuration());
            });
        } else {
            durationWriter = ((outputWriter, record) -> {
                DurationPreprocessor.writeDuration(outputWriter, record.getReceiverId(), record.getDuration());
            });
        }
    }
    
    public void run(String input, String output) throws IOException {
        Writer outputWriter = Files.newBufferedWriter(Paths.get(output));
        writeln(outputWriter, "user" + Constants.SEPARATOR + "duration");
        readInputAndDoStuff(input, line -> durationWriter.writeDuration(outputWriter, CallRecord.read(line)));
        flushAndClose(outputWriter);
    }
    
    private static void writeDuration(Writer outputWriter, int userId, int duration) throws IOException {
        writeln(outputWriter, userId + Constants.SEPARATOR + duration);
    }
    
    private interface IDurationWriter {
        void writeDuration(Writer outputWriter, CallRecord record) throws IOException;
    }
    
}
