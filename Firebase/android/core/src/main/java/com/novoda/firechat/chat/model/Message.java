package com.novoda.firechat.chat.model;

public class Message {

    private String user;
    private String body;
    private long timestamp;

    @SuppressWarnings("unused") //Used by Firebase
    public Message() {
    }

    public Message(String user, String body) {
        this.user = user;
        this.body = body;
        this.timestamp = System.currentTimeMillis(); //TODO move timestamp db side ?
    }

    @Override
    public String toString() {
        return user + ": " + body;
    }

    public String getUser() {
        return user;
    }

    public String getBody() {
        return body;
    }

    public long getTimestamp() {
        return timestamp;
    }

}
