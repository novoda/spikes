package com.novoda.enews;

import retrofit2.Call;
import retrofit2.http.*;

interface MailChimpWebService {

    @GET("lists")
    Call<ApiLists> getLists();

    @POST("campaigns")
    Call<ApiCampaignResult> postCampaign(
            @Body CampaignSettings campaignSettings
    );

    @PUT("campaigns/{id}/content")
    Call<ApiCampaignContentResult> putCampaignContent(
            @Path("id") String id,
            @Body CampaignContent campaignContent
    );

    @POST("campaigns/{id}/actions/schedule")
    Call<Void> postCampaignSchedule(
            @Path("id") String id,
            @Body CampaignSchedule campaignSchedule
    );
}
