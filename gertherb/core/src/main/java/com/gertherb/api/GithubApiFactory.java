package com.gertherb.api;

import org.scribe.services.Base64Encoder;

import retrofit.Endpoint;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;

import static retrofit.RestAdapter.LogLevel.FULL;

public class GithubApiFactory {

    private static final String X_OAUTH_BASIC = "x-oauth-basic";
    private static final String GITHUB_API_URL = "https://api.github.com";
    private static final String APPLICATION_VND_GITHUB_V3_JSON = "application/vnd.github.v3+json";
    private static final String ACCEPT = "Accept";
    private static final String AUTHORIZATION = "Authorization";
    private static final String BASIC = "Basic ";

    public GithubApi createAuthorizationApi(String username, String password) {
        Endpoint endpoint = new Endpoint() {
            @Override
            public String getUrl() {
                return GITHUB_API_URL;
            }

            @Override
            public String getName() {
                return "Live";
            }
        };
        RequestInterceptor interceptor = createAuthorizationRequestInterceptor(username, password);
        RestAdapter restAdapter = createRestAdapter(endpoint, interceptor);
        return restAdapter.create(GithubApi.class);
    }

    public GithubApi createLoggedInApi(String token) {
        return createAuthorizationApi(token, X_OAUTH_BASIC);
    }

    private RequestInterceptor createAuthorizationRequestInterceptor(final String username, final String password) {
        return new RequestInterceptor() {
            @Override
            public void intercept(RequestFacade request) {
                request.addHeader(ACCEPT, APPLICATION_VND_GITHUB_V3_JSON);
                request.addHeader(AUTHORIZATION, BASIC + Base64Encoder.getInstance().encode((username + ":" + password).getBytes()));
            }
        };
    }

    private RestAdapter createRestAdapter(Endpoint endpoint, RequestInterceptor interceptor) {
        return new RestAdapter.Builder()
                .setEndpoint(endpoint)
                .setRequestInterceptor(interceptor)
                .setLogLevel(FULL)
                .build();
    }

}
