package com.novoda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.novoda.enews.*;

import java.time.LocalDateTime;
import java.util.stream.Stream;

public class RunnerMain implements RequestHandler<Request, Response> {

    public static void main(String[] args) {
        if (args.length == 0 || args.length < 2) {
            throw new IllegalStateException("You need to pass a Slack token as the first arg and MailChimp API token as the second.");
        }
        Request request = new Request(args[0], args[1]);
        new RunnerMain().handleRequest(request, null);
    }

    @Override
    public Response handleRequest(Request input, Context context) {
        String slackToken = input.getSlackToken();
        String mailChimpApiKey = input.getMailChimpApiKey();

        Scraper scraper = new Scraper.Factory().newInstance(slackToken);
        HtmlGenerator htmlGenerator = new HtmlGenerator.Factory().newInstance();
        NewsletterPublisher newsletterPublisher = new NewsletterPublisher.Factory().newInstance(mailChimpApiKey);
        ArticleEditor articleEditor = new ArticleEditor.Factory().newInstance();

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

        JsonObject jsonObject = new JsonObject();
        jsonObject.add("message", new JsonPrimitive("complete!"));
        return new Response(jsonObject.getAsJsonObject().toString());
    }
}
