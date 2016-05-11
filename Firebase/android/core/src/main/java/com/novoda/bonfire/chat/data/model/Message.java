package com.novoda.bonfire.chat.data.model;

public class Message {

    private com.novoda.bonfire.login.data.model.User author;
    private String body;
    private long timestamp;

    @SuppressWarnings("unused") //Used by Firebase
    public Message() {
    }

    public Message(com.novoda.bonfire.login.data.model.User author, String body) {
        this.author = author;
        this.body = body;
        this.timestamp = System.currentTimeMillis(); //TODO move timestamp db side ?
    }

    public com.novoda.bonfire.login.data.model.User getAuthor() {
        return author;
    }

    public String getBody() {
        return body;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
