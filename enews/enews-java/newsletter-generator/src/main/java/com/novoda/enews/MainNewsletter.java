package com.novoda.enews;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

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
        Scraper scraper = new Scraper.Factory().newInstance(slackToken);
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().minusDays(7);
        Stream<ChannelHistory.Message> messageStream = scraper.scrape(start, end);
        String html = new HtmlGenerator().generate(messageStream);

        String mailChimpToken = args[1];

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new AuthHeaderInterceptor(mailChimpToken))
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl("https://us5.api.mailchimp.com/3.0/")
                .addConverterFactory(new ConverterFactory())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        MailChimpWebService webService = retrofit.create(MailChimpWebService.class);

        new NewsletterGenerator(webService).generate(html);
    }
}