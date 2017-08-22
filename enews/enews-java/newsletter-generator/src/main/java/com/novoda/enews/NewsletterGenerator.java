package com.novoda.enews;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

import java.io.IOException;

class NewsletterGenerator {
    private final MailChimpWebService webService;
    private final String listId;

    static class Factory {
        NewsletterGenerator newInstance(String mailChimpToken) {
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(new AuthHeaderInterceptor(mailChimpToken))
                    .build();
            Retrofit retrofit = new Retrofit.Builder()
                    .client(okHttpClient)
                    .baseUrl("https://us5.api.mailchimp.com/3.0/")
                    .addConverterFactory(new ConverterFactory())
                    .build();
            MailChimpWebService webService = retrofit.create(MailChimpWebService.class);

            return new NewsletterGenerator(webService, "187645ff44");
        }
    }

    NewsletterGenerator(MailChimpWebService webService, String listId) {
        this.webService = webService;
        this.listId = listId;
    }

    void generate(String html) {
        Response<ApiCampaignResult> campaignResultResponse = postCampaign();
        if (campaignResultResponse.isSuccessful()) {
            putCampaignContent(campaignResultResponse.body().id, html);
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
        CampaignSettings settings = new CampaignSettings(listId, "Test #eNews from Java", "Novoda", "no-reply@novoda.com");
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
}
