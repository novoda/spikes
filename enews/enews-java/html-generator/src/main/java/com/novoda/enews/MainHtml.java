package com.novoda.enews;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.stream.Stream;

public class MainHtml {

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
        HtmlGenerator htmlGenerator = new HtmlGenerator.Factory().newInstance();
        ArticleEditor articleEditor = new ArticleEditor.Factory().newInstance();

        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().minusDays(6);
        Stream<ChannelHistory.Message> messageStream = scraper.scrape(start, end);
        Stream<Article> articleStream = articleEditor.generateArticle(messageStream);
        String html = htmlGenerator.generate(articleStream);

        System.out.println(html);
        Files.write(Paths.get("./build/enews-debug.html"), html.getBytes());
    }
}