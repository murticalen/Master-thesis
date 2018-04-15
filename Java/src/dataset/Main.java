package dataset;

import dataset.util.MulticomLandlineDatasetPreprocessor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Main {

    private static final String MULTICOM_LANDLINE_INPUT = "";
    private static final String OUTPUT_FILE = "";

    public static void main(String[] args) throws Exception {

        var preprocessor = new MulticomLandlineDatasetPreprocessor();
        preprocessor.preProcessDataset(MULTICOM_LANDLINE_INPUT, OUTPUT_FILE);

    }
}
