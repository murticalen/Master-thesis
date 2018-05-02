package main.java.dataset.util;

import main.java.dataset.intervals.CallIntervals;

import java.io.*;
import java.text.SimpleDateFormat;

public class MulticomLandlineDatasetPreprocessor extends AbstractDatasetPreprocessor {

    public MulticomLandlineDatasetPreprocessor() {
        super();
        this.inputDateFormatter = new SimpleDateFormat("dd.MM.yy hh:mm:ss");
        this.weekdayFormatter = new SimpleDateFormat("EE");
    }

    @Override
    public void preProcessDataset(String inputPath, String outputFile) throws Exception {

        var writer = new BufferedWriter(new FileWriter(outputFile));
        writer.write(CallRecord.HEADER);
        writer.write(System.lineSeparator());
        writer.flush();

        for (int i = 1; i <= 5; i++) {
            var reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(inputPath + "/pozivi0" + i + ".dsv"))));
            //skip header
            String line = reader.readLine();
            System.out.println(i);
            while ((line = reader.readLine()) != null) {
                CallRecord record = parseLine(line);
                writer.write(record.toString());
                writer.write(System.lineSeparator());
                writer.flush();
            }
            reader.close();
        }

        writer.close();
    }

    private CallRecord parseLine(String line) {
        String[] call = line.split(";");
        String id = call[1];
        String caller = sanitize(call[11]);
        String receiver = sanitize(call[12]);
        String timestamp = call[4];
        String duration = call[13];
        Integer.parseInt(duration);
        int weekDay = calculateWeekday(timestamp);
        return new CallRecord(id, caller, receiver, duration, timestamp, weekDay);
    }

    /*
    0----->"BROJ"
    1----->"TRANS_ID"
    2----->"BUYER_ID_MASK"
    3----->"DEBTOR_ID_MASK"
    4----->"TRANS_DATE"
    5----->"OFFER_CODE_MASK"
    6----->"SUBSCRIPTION_CODE_MASK"
    7----->"CHARGE_CODE_MASK"
    8----->"AMOUNT"
    9----->"ORIGIN"
    10----->"ITEM_CALL_DIRECTION"
    11----->"ITEM_CALLER_NUMBER_MASK"
    12----->"ITEM_CALLEE_NUMBER_MASK"
    13----->"ITEM_CALL_DURATION"
    14----->"ITEM_ORIGINATING_CARRIER_ID"
    15----->"ITEM_TERMINATING_CARRIER_ID"
    16----->"COMPUTED_ZONE_MASK"
    17----->"COMPUTED_DESTINATION"
    18----->"COMPUTED_PRICE_PER_MINUTE"
    19----->"COMPUTED_ON_PEAK_PRICE_PER_M"
    20----->"COMPUTED_OFF_PEAK_PRICE_PER_M"
    21----->"COMPUTED_ESTABLISHMENT_FEE"
    22----->"PARAMETER_PACKET_MASK"
    23----->"PARAMETER_SERVICE_GROUP"
    24----->"COMPUTED_ROUNDED_DURATION"
     */
}
