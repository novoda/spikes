package com.novoda.enews;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface SlackWebService {

    @GET("channels.history")
    Call<ApiPagedChannelHistory> getChannelHistory(
            @Query("token") String token,
            @Query("channel") String channel,
            @Query("latest") String latest,
            @Query("count") int count);

}
