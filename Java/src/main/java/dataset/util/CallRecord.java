package main.java.dataset.util;

import main.java.social_network.CallSocialInfo;
import main.java.social_network.NetworkExtractor;

import java.util.Objects;

public class CallRecord {

    private String id;
    private String callerId;
    private String receiverId;
    private int duration;
    /**
     * IMPLIED TIMESTAMP FORMAT: dd.MM.yy hh:mm:ss
     */
    private String callTime;
    private int weekDay;
    public static final String SEP = ";";
    public static final String HEADER = "id" + SEP + "callerId" + SEP + "receiverId" + SEP + "duration" + SEP + "callTime" + SEP + "weekDay";

    public CallRecord(String id, String callerId, String receiverId, int duration, String callTime, int weekDay) {
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

    public String getCallerId() {
        return callerId;
    }

    public String getReceiverId() {
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

    public static CallRecord read(String line) {
        String[] call = line.split(";");
        String id = call[0];
        String caller = call[1];
        String receiver = call[2];
        int duration = Integer.parseInt(call[3]);
        String timestamp = call[4];
        int weekDay = Integer.parseInt(call[5]);
        return new CallRecord(id, caller, receiver, duration, timestamp, weekDay);
    }

    public static String extractCallerId(String line)
    {
        return line.split(";")[1];
    }

    public static CallSocialInfo extractSocialInfo(String line){
        String[] call = line.split(";");
        return new CallSocialInfo(Long.parseLong(call[1]), Long.parseLong(call[2]));
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
