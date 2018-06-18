package main.java.dataset.preprocess;

import java.io.IOException;

public class GeneratedCDRPreprocessor extends AbstractPreprocessor {

    public GeneratedCDRPreprocessor() {
        super();
    }

    @Override
    public void preProcessData(String inputPath, String outputFile) throws IOException {
        readInputAndDoStuff(inputPath, line -> {

        });
    }
}
