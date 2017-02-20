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

package com.amazonaws.cognito.sync.demo.client;

import android.util.Log;

import com.amazonaws.cognito.sync.demo.Identifiers;
import com.amazonaws.cognito.sync.demo.client.cognito.CognitoTokenRequestData;
import com.amazonaws.cognito.sync.demo.client.cognito.CognitoTokenResponseData;
import com.amazonaws.cognito.sync.demo.client.cognito.CognitoTokenResponseHandler;
import com.amazonaws.cognito.sync.demo.client.login.LoginRequestData;
import com.amazonaws.cognito.sync.demo.client.login.LoginResponseHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ServerApiClient {

    private static final String LOG_TAG = "ServerApiClient";

    private final String baseUrl;
    private final String appName;
    private final Identifiers identifiers;
    private final OkHttpClient client;

    public ServerApiClient(String baseUrl, String appName, Identifiers identifiers) {
        this.baseUrl = baseUrl;
        this.appName = appName.toLowerCase();
        this.identifiers = identifiers;
        client = new OkHttpClient();
    }

    private ResponseData sendRequest(String url, ResponseHandler reponseHandler) throws IOException {
        Log.i(LOG_TAG, "Sending Request : [" + url + "]");

        Request request = new Request.Builder().url(url).build();
        Response response = client.newCall(request).execute();

        return reponseHandler.handleResponse(response);
    }

    /**
     * Gets a token from the sample Cognito developer authentication
     * application. The registered key is used to secure the communication.
     */
    private ResponseData getToken(Map<String, String> logins, String identityId, String endpoint, ResponseHandler responseHandler) {
        String uid = identifiers.getUidForDevice();
        String key = identifiers.getKeyForDevice();

        RequestData getTokenRequestData = new CognitoTokenRequestData(baseUrl, endpoint, uid, key, logins, identityId);

        // TODO: You can cache the open id token as you will have the control
        // over the duration of the token when it is issued. Caching can reduce
        // the communication required between the app and your backend
        return processRequest(getTokenRequestData, responseHandler);
    }

    /**
     * Gets a token from the sample Cognito developer authentication
     * application. The registered key is used to secure the communication.
     */
    public CognitoTokenResponseData getCognitoToken(Map<String, String> logins, String identityId) {
        String key = identifiers.getKeyForDevice();
        return (CognitoTokenResponseData) getToken(logins, identityId, "gettoken", new CognitoTokenResponseHandler(key));
    }

    public ResponseData getFirebaseToken() {
        return getToken(new HashMap<String, String>(), null, "getfirebasetoken", new ResponseHandler());
    }

    /**
     * Using the given username and password, securily communictes the Key for
     * the user's account.
     */
    public ResponseData login(String username, String password) {
        String uid = identifiers.getUidForDevice();
        String decryptionKey = computeDecryptionKey(username, password, baseUrl);
        LoginRequestData loginRequest = new LoginRequestData(baseUrl, uid, username, decryptionKey);
        ResponseHandler handler = new LoginResponseHandler(decryptionKey);

        return processRequest(loginRequest, handler);
    }

    private String computeDecryptionKey(String username, String password, String baseUrl) {
        String salt = username + appName + HttpUrl.parse(baseUrl).host();
        return Utilities.getSignature(salt, password);
    }

    private ResponseData processRequest(RequestData requestData, ResponseHandler handler) {
        ResponseData responseData = null;
        int retries = 2;
        do {
            try {
                responseData = sendRequest(requestData.buildRequestUrl(), handler);
                if (responseData.requestWasSuccessful()) {
                    return responseData;
                } else {
                    Log.w(LOG_TAG,
                            "Request to Cognito Sample Developer Authentication Application failed with Code: ["
                                    + responseData.getResponseCode() + "] Message: ["
                                    + responseData.getResponseMessage() + "]");
                }
            } catch (IOException e) {
                Log.e(LOG_TAG, "while processing request", e);
            }
        } while (retries-- > 0);

        return responseData;
    }

}
