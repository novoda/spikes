package com.novoda.enews;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.stream.Stream;

public class Runner {

    /**
     * @param args https://api.slack.com/docs/oauth-test-tokens
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            throw new IllegalStateException("You need to pass a Slack token as the first arg. See https://api.slack.com/web");
        }
        String slackToken = args[0];

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://slack.com/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        SlackWebService slackWebService = retrofit.create(SlackWebService.class);
        SlackHistoryFetcher slackHistoryFetcher = SlackHistoryFetcher.from(slackWebService, slackToken);
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().minusDays(7);
        Scraper scraper = new Scraper(slackHistoryFetcher);

        scraper.scrape(start, end)
                .forEach(System.out::println);
    }

}
