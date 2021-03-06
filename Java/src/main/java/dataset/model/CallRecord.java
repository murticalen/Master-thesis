package main.java.dataset.model;

import main.java.configuration.Constants;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class CallRecord {

    private String id;
    private int callerId;
    private int receiverId;
    private int duration;
    private String callTime;
    private int weekDay;

    public static final DateFormat TIMESTAMP_FORMATTER = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
    public static final String SEP = Constants.SEPARATOR;
    public static final String HEADER = "id" + SEP + "callerId" + SEP + "receiverId" + SEP + "duration" + SEP + "callTime" + SEP + "weekDay";

    public CallRecord(String id, int callerId, int receiverId, int duration, String callTime, int weekDay) {
        this.id = id;
        this.callerId = callerId;
        this.receiverId = receiverId;
        this.duration = duration;
        this.callTime = callTime;
        this.weekDay = weekDay;
    }

    public String getId() {
        return id;
    }

    public int getCallerId() {
        return callerId;
    }

    public int getReceiverId() {
        return receiverId;
    }

    public int getDuration() {
        return duration;
    }

    public void setCallTime(String callTime) {
        this.callTime = callTime;
    }

    public String getCallTime() {
        return callTime;
    }

    public int getWeekDay() {
        return weekDay;
    }

    public String toString() {
        return id + SEP + callerId + SEP + receiverId + SEP + duration + SEP + callTime + SEP + weekDay;
    }

    public static CallRecord read(String formattedLine) {
        String[] call = formattedLine.split(";");
        String id = call[0];
        int caller = Integer.parseInt(call[1]);
        int receiver = Integer.parseInt(call[2]);
        int duration = Integer.parseInt(call[3]);
        String timestamp = call[4];
        int weekDay = Integer.parseInt(call[5]);
        return new CallRecord(id, caller, receiver, duration, timestamp, weekDay);
    }

    public static Date getCallTimeStamp(String line) throws ParseException {
        return CallRecord.TIMESTAMP_FORMATTER.parse(line.split(";")[4]);
    }

    public static int extractCallerId(String line) {
        return Integer.parseInt(line.split(";")[1]);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CallRecord record = (CallRecord) o;
        return Objects.equals(id, record.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
