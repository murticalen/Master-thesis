package main.java.social_network;

import java.util.Objects;

public class CallSocialInfo {
    private long callerId;
    private long receiverId;

    public CallSocialInfo(long callerId, long receiverId) {
        this.callerId = callerId;
        this.receiverId = receiverId;
    }

    public long getCallerId() {
        return callerId;
    }

    public long getReceiverId() {
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
        return callerId+"->"+receiverId;
    }
}
