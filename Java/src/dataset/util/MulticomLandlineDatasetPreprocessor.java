package dataset.util;

import java.io.BufferedWriter;
import java.io.FileWriter;

public class MulticomLandlineDatasetPreprocessor implements DatasetPreprocessor {

    @Override
    public void preProcessDataset(String inputPath, String outputFile) throws Exception {

        var writer = new BufferedWriter(new FileWriter(outputFile));
        writer.write(CallRecord.HEADER);
        writer.flush();

        writer.close();

    }
}
