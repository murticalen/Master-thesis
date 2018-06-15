package main.java.dataset.intervals;

import main.java.dataset.DatasetMain;
import main.java.dataset.util.AbstractReader;
import main.java.dataset.model.CallRecord;
import main.java.dataset.util.KMeans;
import main.java.dataset.util.Sampler;

import java.io.IOException;
import java.util.List;

public class DayIntervalCreator extends AbstractReader {

    public static final int SAMPLE_SIZE = 100000;

    public static void main(String[] args) throws IOException {
        DateFilter.ignoredDates.add("17-01-01");

        Sampler sampler = new Sampler(SAMPLE_SIZE, DatasetMain.TOTAL_SIZE);
        List<CallRecord> records = sampler.getAllRecords(DatasetMain.OUTPUT_FILE, DateFilter::isValidDate);
        int totalRecords = records.size();

        double[][] times = new double[totalRecords][];
        for (int i = 0, size = times.length; i < size; i++) {
            times[i] = new double[]{IntervalHelper.calculateTimePoint(records.get(i).getCallTime(), CallRecord.TIMESTAMP_FORMATTER)};
        }
        records.clear();
        System.out.println("FINISHED READING");
        System.out.println(records.size());
        System.out.println(times.length);

        KMeans kMeans = new KMeans(1, 1000, 1, totalRecords);
        for (int i = 2; i < 100; i++) {
            kMeans.run(times, i);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(i);
            stringBuilder.append(CallRecord.SEP);
            stringBuilder.append(kMeans.getError());
//            for (double[] centroid : kMeans.getCentroids()) {
//                stringBuilder.append(CallRecord.SEP);
//                stringBuilder.append(centroid[0]);
//            }
            System.out.println(stringBuilder);
        }
    }
}
