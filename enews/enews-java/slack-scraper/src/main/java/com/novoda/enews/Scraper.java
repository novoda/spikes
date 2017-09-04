package com.novoda.enews;

import org.jetbrains.annotations.NotNull;
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
                .map(message -> new ChannelHistory.Message(message.getText()
                        .replace("<#C0YNBKANM|enews>", "#eNews")
                        .replace("<#C1V389HGB|enews>", "#eNews"), message.getImageUrl(), message.getPageLink()))
                .filter(message -> {
                    String messageText = message.getText().toLowerCase();
                    return messageText.contains("#enews")
                            &&
                            messageText.contains("http");

                });
    }

    public static class Factory {

        @NotNull
        public Scraper newInstance(String slackToken) {
            return newInstance(slackToken, false);
        }

        @NotNull
        public Scraper newInstance(String slackToken, boolean runLocally) {
            if (runLocally) {
                return newMockServiceInsstance(slackToken);
            } else {
                return newLiveServiceInstance(slackToken);
            }
        }

        @NotNull
        private Scraper newMockServiceInsstance(String slackToken) {
            SlackWebService slackWebService = new MockSlackWebService();
            SlackHistoryFetcher slackHistoryFetcher = SlackHistoryFetcher.from(slackWebService, slackToken);
            return new Scraper(slackHistoryFetcher);
        }

        @NotNull
        private Scraper newLiveServiceInstance(String slackToken) {
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
