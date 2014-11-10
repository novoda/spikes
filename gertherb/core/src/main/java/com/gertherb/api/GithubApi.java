package com.gertherb.api;

import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.PUT;
import retrofit.http.Path;
import rx.Observable;

public interface GithubApi {

    @PUT("/authorizations/clients/{client_id}")
    Observable<TokenResponse> authorize(@Path("client_id") String clientId, @Body TokenRequest request);

    @GET("/user/repos")
    Observable<String> getUserDetails();

}
