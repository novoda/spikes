package com.novoda.pianohero;

class Message {

    private final CharSequence value;

    static Message empty() {
        return new Message("");
    }

    Message(CharSequence value) {
        this.value = value;
    }

    CharSequence getValue() {
        return value;
    }
}
