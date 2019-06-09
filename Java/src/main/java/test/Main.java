package main.java.test;

import main.java.configuration.Constants;
import main.java.dataset.intervals.CallIntervals;
import main.java.dataset.intervals.IntervalHelper;
import main.java.dataset.model.CallRecord;
import main.java.dataset.model.Tuple;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Main {
    
    
    public static void main(String[] args) {
        
        CallIntervals.initialize();
        
        //CallIntervals.initialize();
        
        //HelperTest.testMinutesFromDecimal();
        //HelperTest.testTimeThroughDay();
        
        //IntervalHelperEvaluator.cdfTest();
        //IntervalHelperEvaluator.cdrTest();
        
        //IntervalHelperEvaluator.callTotalTest();
        
        Map<String, String> englishCroatianIntervals = new LinkedHashMap<>();
        englishCroatianIntervals.put(Constants.AFTER_MIDNIGHT, "Poslije ponoći");
        englishCroatianIntervals.put(Constants.LATE_NIGHT, "Kasna noć");
        englishCroatianIntervals.put(Constants.EARLY_MORNING, "Rano jutro");
        englishCroatianIntervals.put(Constants.MORNING, "Jutro");
        englishCroatianIntervals.put(Constants.PRE_NOON, "Prije podne");
        englishCroatianIntervals.put(Constants.NOON, "Podne");
        englishCroatianIntervals.put(Constants.AFTERNOON, "Poslije podne");
        englishCroatianIntervals.put(Constants.PRE_EVENING, "Predvečerje");
        englishCroatianIntervals.put(Constants.EVENING, "Večer");
        englishCroatianIntervals.put(Constants.LATE_EVENING, "Kasna večer");
        
        HashMap<Integer, String> weekDayCroatianDay = new LinkedHashMap<>();
        weekDayCroatianDay.put(1, "Ponedjeljak");
        weekDayCroatianDay.put(2, "Utorak");
        weekDayCroatianDay.put(3, "Srijeda");
        weekDayCroatianDay.put(4, "Četvrtak");
        weekDayCroatianDay.put(5, "Petak");
        weekDayCroatianDay.put(6, "Subota");
        weekDayCroatianDay.put(7, "Nedjelja");
        
        double t = IntervalHelper.calculateTimePoint(0, 53, 23);
        
        System.out.println("i;v;interval");
        
        for (double i = -24; i <= 48; i += 0.01) {
            double v = IntervalHelper.pdf(i, t, 1);
            System.out.println(String.format("%.5f", i) + ";" + (v > 1e-5 ? v : 0) + ";" + englishCroatianIntervals.get(intervalForTime(i)));
        }
        
        System.out.println("day;interval;value");
        
        String                            time           = "17-12-01 0:53:23";
        CallRecord                        record         = new CallRecord("4", 1, 2, 55, time, 1);
        Map<Integer, Map<String, Double>> dailyIntervals = IntervalHelper.extractIntervals(record);
        for (Map.Entry<Integer, Map<String, Double>> dayIntervals : dailyIntervals.entrySet()) {
            for (Map.Entry<String, Double> intervalValue : dayIntervals.getValue().entrySet()) {
                System.out.println(weekDayCroatianDay.get(dayIntervals.getKey()) + ";" + englishCroatianIntervals.get(intervalValue.getKey()) + ";" + intervalValue.getValue());
            }
        }
        
        for (String interval : englishCroatianIntervals.values()) {
            //System.out.print("\"" + interval + "\",");
        }
    }
    
    public static String intervalForTime(double time) {
        if (time < 0) {
            time += 24;
        }
        if (time > 24) {
            time -= 24;
        }
        for (Map.Entry<Tuple<Double>, String> entry : CallIntervals.timeIntervalMap.entrySet()) {
            if (time <= entry.getKey().getV2()) {
                return entry.getValue();
            }
        }
        return null;
    }
    
}
