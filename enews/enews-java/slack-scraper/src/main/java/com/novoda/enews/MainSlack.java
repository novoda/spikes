package com.novoda.enews;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class MainSlack {

    /**
     * First param is the slack api key
     * Second param is a boolean if you want to use mock data
     *
     * @param args https://api.slack.com/docs/oauth-test-tokens
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            throw new IllegalStateException("You need to pass a Slack token as the first arg. See https://api.slack.com/web");
        }
        boolean runLocally = Boolean.parseBoolean(args[1]);
        if (runLocally) {
            System.out.println("Running using local mock data");
        }

        String slackToken = args[0];
        Scraper scraper = new Scraper.Factory().newInstance(slackToken, runLocally);

        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().minusDays(6);
        List<ChannelHistory.Message> messages = scraper.scrape(start, end).collect(Collectors.toList());
        messages.forEach(System.out::println);
        System.out.println("Total was: " + messages.size());
    }

}
