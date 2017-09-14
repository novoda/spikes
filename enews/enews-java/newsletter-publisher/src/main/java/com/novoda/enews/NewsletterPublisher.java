package com.novoda.enews;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

import java.io.IOException;
import java.time.LocalDateTime;

public class NewsletterPublisher {
    private final MailChimpWebService webService;
    private final String listId;
    private final NewsletterScheduler newsletterScheduler;

    public static class Factory {
        public NewsletterPublisher newInstance(String mailChimpToken) {
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(new AuthHeaderInterceptor(mailChimpToken))
                    .build();
            Retrofit retrofit = new Retrofit.Builder()
                    .client(okHttpClient)
                    .baseUrl("https://us5.api.mailchimp.com/3.0/")
                    .addConverterFactory(new ConverterFactory())
                    .build();
            MailChimpWebService webService = retrofit.create(MailChimpWebService.class);

            NewsletterScheduler newsletterScheduler = new NewsletterScheduler(webService);

            return new NewsletterPublisher(webService, "187645ff44", newsletterScheduler);
        }
    }

    NewsletterPublisher(MailChimpWebService webService, String listId, NewsletterScheduler newsletterScheduler) {
        this.webService = webService;
        this.listId = listId;
        this.newsletterScheduler = newsletterScheduler;
    }

    public void publish(String html, LocalDateTime atLocalDateTime) {
        Response<ApiCampaignResult> campaignResultResponse = postCampaign();
        if (campaignResultResponse.isSuccessful()) {
            String id = campaignResultResponse.body().id;
            putCampaignContent(id, html);
            scheduleNewsletter(id, atLocalDateTime);
        } else {
            ResponseBody responseBody = campaignResultResponse.errorBody();
            try {
                System.err.println(responseBody.string());
            } catch (IOException e) {
                throw new IllegalStateException("We failed to debug.", e);
            }
            throw new IllegalStateException("We failed " + campaignResultResponse.code());
        }
    }

    private Response<ApiCampaignResult> postCampaign() {
        CampaignSettings settings = new CampaignSettings(listId, "Novoda's weekly #eNews", "Novoda", "no-reply@novoda.com");
        Call<ApiCampaignResult> postCampaign = webService.postCampaign(settings);
        try {
            return postCampaign.execute();
        } catch (IOException e) {
            throw new IllegalStateException("FooBar ", e);
        }
    }

    private Response<ApiCampaignContentResult> putCampaignContent(String campaignId, String html) {
        Call<ApiCampaignContentResult> postCampaign = webService.putCampaignContent(campaignId, new CampaignContent(html));
        try {
            return postCampaign.execute();
        } catch (IOException e) {
            throw new IllegalStateException("FooBar ", e);
        }
    }

    private void scheduleNewsletter(String id, LocalDateTime atLocalDateTime) {
        newsletterScheduler.schedule(id, atLocalDateTime);
    }
}
