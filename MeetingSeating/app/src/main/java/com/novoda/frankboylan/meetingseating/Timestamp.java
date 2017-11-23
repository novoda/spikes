package com.novoda.frankboylan.meetingseating;

public class Timestamp {
    private final Long timestamp;

    public Timestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Timestamp(String timestamp) {
        this.timestamp = Long.parseLong(timestamp);
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public static final Timestamp INVALID_TIMESTAMP = new Timestamp(-1L);
}
