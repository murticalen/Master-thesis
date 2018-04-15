package dataset.util;

public class CallRecord {

    private String id;
    private String callerId;
    private String receiverId;
    private String location;
    private String duration;
    private String callTime;
    public static final String DELIMITER = ";";
    public static final String HEADER = "id" + DELIMITER + "callerId" + DELIMITER + "receiverId" + DELIMITER + "location" + DELIMITER + "duration" + DELIMITER + "callTime";

    public CallRecord(String id, String callerId, String receiverId, String location, String duration, String callTime) {
        this.id = id;
        this.callerId = callerId;
        this.receiverId = receiverId;
        this.location = location;
        this.duration = duration;
        this.callTime = callTime;
    }

    public String toString() {
        return id + DELIMITER + callerId + DELIMITER + receiverId + DELIMITER + location + DELIMITER + duration + DELIMITER + callTime;
    }
}
