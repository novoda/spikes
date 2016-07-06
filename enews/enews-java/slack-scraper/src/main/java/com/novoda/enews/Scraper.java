package com.novoda.enews;

import java.time.LocalDateTime;
import java.util.stream.Stream;

public class Scraper {

    private final SlackHistoryFetcher slackHistoryFetcher;

    public Scraper(SlackHistoryFetcher slackHistoryFetcher) {
        this.slackHistoryFetcher = slackHistoryFetcher;
    }

    public Stream<ChannelHistory.Message> scrape(LocalDateTime start, LocalDateTime end) {
        ChannelHistory channelHistory = slackHistoryFetcher.getChannelHistory(start, end);

        System.out.println(channelHistory.getHistoryFrom() + " / " + channelHistory.getHistoryTo());
        return channelHistory
                .getMessages()
                .parallelStream()
                .map(message -> new ChannelHistory.Message(message.toString().replace("#C0YNBKANM", "#eNews")))
                .filter(message -> {
                    String messageText = message.toString().toLowerCase();
                    return messageText.contains("#enews")
                            &&
                            messageText.contains("http");

                });
    }
}
