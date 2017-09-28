package com.novoda.enews;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.stream.Stream;

public class MainNewsletter {

    /**
     * @param args https://api.slack.com/docs/oauth-test-tokens, https://us5.admin.mailchimp.com/account/api/
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        if (args.length == 0 || args.length < 2) {
            throw new IllegalStateException("You need to pass a Slack token as the first arg and MailChimp API token as the second.");
        }
        String slackToken = args[0];
        String mailChimpToken = args[1];
        Scraper scraper = new Scraper.Factory().newInstance(slackToken);
        HtmlGenerator htmlGenerator = new HtmlGenerator.Factory().newInstance();
        ArticleEditor articleEditor = new ArticleEditor.Factory().newInstance();
        NewsletterPublisher newsletterPublisher = new NewsletterPublisher.Factory().newInstance(mailChimpToken);

        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().minusDays(6);
        Stream<ChannelHistory.Message> messageStream = scraper.scrape(start, end);
        Stream<Article> articleStream = articleEditor.generateArticle(messageStream);
        String html = htmlGenerator.generate(articleStream);
        LocalDateTime atLocalDateTime = LocalDateTime.now().plusDays(1).plusHours(1);
        newsletterPublisher.publish(html, atLocalDateTime);
        // Time warp campaigns have to be 24 hours in the future
        // The scheduling is completely hidden here and is a TODO
        // - because if someone set up a cron job they cannot *fully* control the time right now
    }
}