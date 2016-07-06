package com.novoda.enews;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.time.LocalDateTime;
import java.util.stream.Stream;

public class Scraper {

    private final SlackHistoryFetcher slackHistoryFetcher;

    Scraper(SlackHistoryFetcher slackHistoryFetcher) {
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

    public static class Factory {

        public Scraper newInstance(String slackToken) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://slack.com/api/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            SlackWebService slackWebService = retrofit.create(SlackWebService.class);
            SlackHistoryFetcher slackHistoryFetcher = SlackHistoryFetcher.from(slackWebService, slackToken);
            return new Scraper(slackHistoryFetcher);
        }

    }
}
