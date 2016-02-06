package com.novoda.enews;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.time.LocalDateTime;

public class Runner {


    public static void main(String[] args) throws IOException {
        String slackToken = args[0];
        if (slackToken == null) {
            throw new IllegalStateException("You need to pass a Slack token as the first arg. See https://api.slack.com/web");
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://slack.com/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        SlackWebService slackWebService = retrofit.create(SlackWebService.class);
        SlackHistoryFetcher slackHistoryFetcher = SlackHistoryFetcher.from(slackWebService, slackToken);

        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().minusDays(365);
        ChannelHistory channelHistory = slackHistoryFetcher.getChannelHistory(start, end);

        System.out.println(channelHistory.getHistoryFrom() + " / " + channelHistory.getHistoryTo());
        channelHistory
                .getMessages()
                .parallelStream()
                .filter(message -> {
                    String messageText = message.toString().toLowerCase();
                    return messageText.contains("#enews")
                            &&
                            messageText.contains("http");

                })
                .forEach(System.out::println);
    }

}
