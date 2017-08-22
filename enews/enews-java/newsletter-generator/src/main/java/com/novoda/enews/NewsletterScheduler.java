package com.novoda.enews;

import retrofit2.Call;

import java.io.IOException;
import java.time.LocalDateTime;

class NewsletterScheduler {

    private final MailChimpWebService webService;

    NewsletterScheduler(MailChimpWebService webService) {
        this.webService = webService;
    }

    public void schedule(String id, LocalDateTime localDateTime) {
        Call<Void> postSchedule = webService.postCampaignSchedule(id, new CampaignSchedule(localDateTime, true));

        try {
            postSchedule.execute();
        } catch (IOException e) {
            throw new IllegalStateException("FooBar ", e);
        }
    }

}
