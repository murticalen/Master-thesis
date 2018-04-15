package dataset.util;

public class CallRecord {

    private String id;
    private String callerId;
    private String receiverId;
    private String location;
    private String duration;
    private String callTime;
    private String originCarrierId;
    private String terminatingCarrierId;
    public static final String DELIMITER = ";";
    public static final String HEADER = "id" + DELIMITER + "callerId" + DELIMITER + "receiverId" + DELIMITER + "location" + DELIMITER + "duration" + DELIMITER + "callTime" + DELIMITER + "originCarrierId" + DELIMITER + "terminatingCarrierId";

    public CallRecord(String id, String callerId, String receiverId, String location, String duration, String callTime, String originCarrierId, String terminatingCarrierId) {
        this.id = id;
        this.callerId = callerId;
        this.receiverId = receiverId;
        this.location = location;
        this.duration = duration;
        this.callTime = callTime;
        this.originCarrierId = originCarrierId;
        this.terminatingCarrierId = terminatingCarrierId;
    }

    public String toString() {
        return id + DELIMITER + callerId + DELIMITER + receiverId + DELIMITER + location + DELIMITER + duration + DELIMITER + callTime + DELIMITER + originCarrierId + DELIMITER + terminatingCarrierId;
    }
}
