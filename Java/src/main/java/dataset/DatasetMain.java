package main.java.dataset;

import main.java.dataset.intervals.CallIntervals;
import main.java.dataset.intervals.DateFilter;
import main.java.dataset.intervals.UserIntervalExtractor;
import main.java.dataset.preprocess.AbstractPreprocessor;
import main.java.dataset.preprocess.MulticomLandlinePreprocessor;
import main.java.dataset.util.*;

import java.io.IOException;
import java.text.ParseException;

public class DatasetMain {

    private static final String MULTICOM_LANDLINE_INPUT = "./../dataset/";
    public static final String OUTPUT_FILE = "./../dataset/combined.csv";
    public static final String ID_REMAP_FILE = "./../dataset/id_remap.csv";
    public static final String USER_PROFILE_FILE = "./../dataset/user_profiles.csv";
    public static final String USER_SPLITTED_OUTPUT = "./../dataset/user_splitted/";
    //sample 100k calls out of 24 mil.
    private static final int SAMPLE_SIZE = 100000;
    public static final int TOTAL_SIZE = 23712942;
    //split the dataset into files of 1M calls and make sure all calls for one user are in the same file
    private static final int MAX_CALLS_PER_FILE = 1000000;

    public static void main(String[] args) throws IOException, ParseException {
        CallIntervals.initialize();


        //NOTE: this is actually a fake map-reduce done on a single computer without the Hadoop framework
        //next two pre-processors do the mapping and the latter one reducing
        //if one is to migrate this to a real map reduce, all it needs to do is parse lines to CallRecord and emit(callerId, record)
        AbstractPreprocessor preprocessor = new MulticomLandlinePreprocessor(ID_REMAP_FILE);
        preprocessor.preProcessData(MULTICOM_LANDLINE_INPUT, OUTPUT_FILE);

        preprocessor = new UserCallsSplitter(MAX_CALLS_PER_FILE);
        preprocessor.preProcessData(OUTPUT_FILE, USER_SPLITTED_OUTPUT);

        UserIntervalExtractor extractor = new UserIntervalExtractor();
        DateFilter.ignoredDates.add("17-01-01");
        //DateFilter.onlyValidDates.add("");
        //extractor.extractAndSaveIntervals(USER_SPLITTED_OUTPUT, USER_PROFILE_FILE);

        Sampler sampler = new Sampler(SAMPLE_SIZE, TOTAL_SIZE);
        //List<CallRecord> samples = sampler.getAllRecords(OUTPUT_FILE);
    }
}
