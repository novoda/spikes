/**
 * Copyright 2010-2014 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 * <p>
 * http://aws.amazon.com/apache2.0
 * <p>
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package com.amazonaws.cognito.sync.devauth.client;

import android.util.Log;

import com.amazonaws.cognito.sync.demo.Cognito;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.OkHttpClient;

/**
 * This class is used to communicate with the sample Cognito developer
 * authentication application sample.
 */
public class ServerApiClient {

    private static final String LOG_TAG = "ServerApiClient";

    /**
     * The host for the sample Cognito developer authentication application.
     */
    private final String host;

    /**
     * The appName declared by the sample Cognito developer authentication
     * application.
     */
    private final String appName;

    public ServerApiClient(String host, String appName) {
        this.host = host;
        this.appName = appName.toLowerCase();
    }

    private static Response sendRequest(String url, ResponseHandler reponseHandler) {
        try {
            Log.i(LOG_TAG, "Sending Request : [" + url + "]");

            okhttp3.Request req = new okhttp3.Request.Builder().url(url).build();
            okhttp3.Response response = new OkHttpClient().newCall(req).execute();

            int responseCode = response.code();
            String responseBody = response.body().string();
            Log.i(LOG_TAG, "Response : [" + responseBody + "]");

            return reponseHandler.handleResponse(responseCode, responseBody);
        } catch (IOException exception) {
            Log.w(LOG_TAG, exception);
            if (exception.getMessage().equals("Received authentication challenge is null")) {
                return reponseHandler.handleResponse(401, "Unauthorized token request");
            } else {
                return reponseHandler.handleResponse(404, "Unable to reach resource at [" + url + "]");
            }
        }
    }

    /**
     * Gets a token from the sample Cognito developer authentication
     * application. The registered key is used to secure the communication.
     */
    private Response getToken(Map<String, String> logins, String identityId, String endpoint, ResponseHandler responseHandler) {
        String uid = Cognito.INSTANCE.getIdentifiers().getUidForDevice();
        String key = Cognito.INSTANCE.getIdentifiers().getKeyForDevice();

        Request getTokenRequest = new GetTokenRequest(host, endpoint, uid, key, logins, identityId);

        // TODO: You can cache the open id token as you will have the control
        // over the duration of the token when it is issued. Caching can reduce
        // the communication required between the app and your backend
        return processRequest(getTokenRequest, responseHandler);
    }

    /**
     * Gets a token from the sample Cognito developer authentication
     * application. The registered key is used to secure the communication.
     */
    public GetTokenResponse getCognitoToken(Map<String, String> logins, String identityId) {
        String key = Cognito.INSTANCE.getIdentifiers().getKeyForDevice();
        return (GetTokenResponse) getToken(logins, identityId, "gettoken", new GetTokenResponseHandler(key));
    }

    public Response getFirebaseToken(String identityId) {
        return getToken(new HashMap<String, String>(), identityId, "getfirebasetoken", new ResponseHandler());
    }

    /**
     * Using the given username and password, securily communictes the Key for
     * the user's account.
     */
    public Response login(String username, String password) {
        String uid = Cognito.INSTANCE.getIdentifiers().getUidForDevice();
        LoginRequest loginRequest = new LoginRequest(host, appName, uid, username, password);
        ResponseHandler handler = new LoginResponseHandler(loginRequest.getDecryptionKey());

        return processRequest(loginRequest, handler);
    }

    private Response processRequest(Request request, ResponseHandler handler) {
        Response response;
        int retries = 2;
        do {
            response = sendRequest(request.buildRequestUrl(), handler);
            if (response.requestWasSuccessful()) {
                return response;
            } else {
                Log.w(LOG_TAG,
                        "Request to Cognito Sample Developer Authentication Application failed with Code: ["
                                + response.getResponseCode() + "] Message: ["
                                + response.getResponseMessage() + "]");
            }
        } while (retries-- > 0);

        return response;
    }

}
