package main.java.dataset.util;

import main.java.dataset.intervals.CallIntervals;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class MulticomLandlineDatasetPreprocessor extends AbstractDatasetPreprocessor {

    private Map<String, Integer> idRemap;
    private int currentId = 0;

    public MulticomLandlineDatasetPreprocessor() {
        super();
        this.inputDateFormatter = new SimpleDateFormat("dd.MM.yy hh:mm:ss");
        this.weekdayFormatter = new SimpleDateFormat("EE");
        idRemap = new HashMap<>();
    }

    @Override
    public void preProcessDataset(String inputPath, String outputFile) throws Exception {

        var writer = new BufferedWriter(new FileWriter(outputFile));
        writeln(writer, CallRecord.HEADER);

        //merge 5 files into 1 large file
        for (int i = 1; i <= 5; i++) {
            System.out.println(i);
            readInputAndDoStuff(inputPath + "/pozivi0" + i + ".dsv", line -> {
                CallRecord record = parseLine(line);
                writeln(writer, record.toString());
            });
        }
        flushAndClose(writer);
    }

    private CallRecord parseLine(String line) {
        String[] call = line.split(";");
        String id = call[1];
        //String caller = (sanitize(call[11]));
        int callerId = getId(sanitize(call[11]));
        //String receiver = (sanitize(call[12]));
        int receiverId = getId(sanitize(call[12]));
        String timestamp = call[4];
        int duration = Integer.parseInt(call[13]);
        int weekDay = calculateWeekday(timestamp);
        return new CallRecord(id, callerId, receiverId, duration, getTimestamp(timestamp), weekDay);
    }

    private int getId(String number) {
        if (!idRemap.containsKey(number)) {
            currentId++;
            idRemap.put(number, currentId);
        }
        return idRemap.get(number);
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
