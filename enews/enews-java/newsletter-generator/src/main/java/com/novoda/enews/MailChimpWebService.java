package com.novoda.enews;

import retrofit2.Call;
import retrofit2.http.*;

public interface MailChimpWebService {

    @Headers("Authorization: apiKey 2adce69f42e1ea8663448f2d62f77fb7-us5")
    @GET("lists")
    Call<ApiPagedChannelHistory> getLists();

    @POST("campaigns")
    Call<ApiPagedChannelHistory> postCampaign(
            @Body Campaign campaign
    );

    @PUT("campaigns/{id}/content")
    Call<ApiPagedChannelHistory> putCampaignContent(
            @Path("id") String id,
            @Body CampaignContent campaignContent
    );
}
