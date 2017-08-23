package com.novoda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.novoda.enews.HtmlGenerator;
import com.novoda.enews.NewsletterPublisher;
import com.novoda.enews.Scraper;

import java.time.LocalDateTime;

public class RunnerMain implements RequestHandler<Request, Response> {

    @Override
    public Response handleRequest(Request input, Context context) {
        String slackToken = input.getSlackToken();
        String mailChimpApiKey = input.getMailChimpApiKey();

        Scraper scraper = new Scraper.Factory().newInstance(slackToken);
        HtmlGenerator htmlGenerator = new HtmlGenerator.Factory().newInstance();
        NewsletterPublisher newsletterPublisher = new NewsletterPublisher.Factory().newInstance(mailChimpApiKey);

        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().minusDays(7);
        String html = htmlGenerator.generate(scraper.scrape(start, end));
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
