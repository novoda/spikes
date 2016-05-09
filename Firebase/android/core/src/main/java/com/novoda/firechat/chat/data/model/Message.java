package com.novoda.firechat.chat.data.model;

public class Message {

    private String author;
    private String body;
    private long timestamp;

    @SuppressWarnings("unused") //Used by Firebase
    public Message() {
    }

    public Message(String author, String body) {
        this.author = author;
        this.body = body;
        this.timestamp = System.currentTimeMillis(); //TODO move timestamp db side ?
    }

    @Override
    public String toString() {
        return author + ": " + body;
    }

    public String getAuthor() {
        return author;
    }

    public String getBody() {
        return body;
    }

    public long getTimestamp() {
        return timestamp;
    }

}
