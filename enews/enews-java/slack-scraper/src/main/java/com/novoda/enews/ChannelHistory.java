package com.novoda.enews;

import java.net.MalformedURLException;
import java.net.URL;
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
        private final String imageUrl;
        private final String pageLink;

        public Message(String text, String imageUrl, String pageLink) {
            this.text = text;
            this.imageUrl = imageUrl;
            this.pageLink = pageLink;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public URL toUrl() {
            int urlStart = text.indexOf("http");
            int urlEnd = text.indexOf('>', urlStart);
            String url = text.substring(urlStart, urlEnd);
            try {
                return new URL(url);
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
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
