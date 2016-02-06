package com.novoda.enews;

import java.time.LocalDateTime;
import java.util.List;

class ChannelHistory {

    private final LocalDateTime historyFrom;
    private final LocalDateTime historyTo;
    private final List<Message> messages;

    public ChannelHistory(LocalDateTime historyFrom, LocalDateTime historyTo, List<Message> messages) {
        this.historyFrom = historyFrom;
        this.historyTo = historyTo;
        this.messages = messages;
    }

    public LocalDateTime getHistoryFrom() {
        return historyFrom;
    }

    public LocalDateTime getHistoryTo() {
        return historyTo;
    }

    public List<Message> getMessages() {
        return messages;
    }

    @Override
    public String toString() {
        return "ChannelHistory{" +
                "historyFrom=" + historyFrom +
                ", historyTo=" + historyTo +
                ", messages=" + messages +
                '}';
    }

    public static class Message {
        private final String text;

        public Message(String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return text;
        }
    }

}
