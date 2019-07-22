package com.novoda.enews;


import org.jetbrains.annotations.Nullable;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;

public class ChannelHistory {

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
        private final String imageUrl;
        private final String pageLink;

        public Message(String text, @Nullable String imageUrl, String pageLink) {
            this.text = text;
            this.imageUrl = imageUrl;
            this.pageLink = pageLink;
        }

        public boolean hasImage() {
            return imageUrl != null;
        }

        @Nullable
        public String getImageUrl() {
            return imageUrl;
        }

        public String getText() {
            return text;
        }

        @Override
        public String toString() {
            return text;
        }

        public String getPageLink() {
            return pageLink;
        }
    }

}
