package com.novoda.enews;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;

class NewsletterGenerator {
    private final MailChimpWebService webService;

    static class Factory {
         NewsletterGenerator newInstance(String mailChimpToken) {
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

            return new NewsletterGenerator(webService);
        }
    }

    NewsletterGenerator(MailChimpWebService webService) {
        this.webService = webService;
    }

    void generate(String html) {
        Response<ApiCampaignResult> campaignResultResponse = postCampaign();
        if(campaignResultResponse.isSuccessful()) {
            putCampaignContent(campaignResultResponse.body().id, html);
        } else {
            throw new IllegalStateException("We failed " + campaignResultResponse.code());
        }
    }

    private Response<ApiCampaignResult> postCampaign() {
        Call<ApiCampaignResult> postCampaign = webService.postCampaign(new CampaignSettings("listId TODO"));
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
