package main.java.social_network;

import java.util.Objects;

public class CallSocialInfo {

    public static boolean DIRECTED = true;
    private int callerId;
    private int receiverId;

    public CallSocialInfo(int callerId, int receiverId) {
        if (DIRECTED) {
            this.callerId = callerId;
            this.receiverId = receiverId;
        } else {
            if (callerId < receiverId) {
                this.callerId = callerId;
                this.receiverId = receiverId;
            } else {
                this.callerId = receiverId;
                this.receiverId = callerId;
            }
        }
    }

    public int getCallerId() {
        return callerId;
    }

    public int getReceiverId() {
        return receiverId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CallSocialInfo that = (CallSocialInfo) o;
        return callerId == that.callerId &&
                receiverId == that.receiverId;
    }

    @Override
    public int hashCode() {

        return Objects.hash(callerId, receiverId);
    }

    @Override
    public String toString() {
        return callerId + "->" + receiverId;
    }
}
