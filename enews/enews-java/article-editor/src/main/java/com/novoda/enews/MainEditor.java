package com.novoda.enews;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.stream.Stream;

public class MainEditor {

    /**
     * @param args https://api.slack.com/docs/oauth-test-tokens
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            throw new IllegalStateException("You need to pass a Slack token as the first arg. See https://api.slack.com/web");
        }
        String slackToken = args[0];
        Scraper scraper = new Scraper.Factory().newInstance(slackToken);
        ArticleEditor articleEditor = new ArticleEditor.Factory().newInstance();

        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().minusDays(6);
        Stream<ChannelHistory.Message> messageStream = scraper.scrape(start, end);
        Stream<Article> articleStream = articleEditor.generateArticle(messageStream);
        articleStream.forEach(System.out::println);
    }

}
