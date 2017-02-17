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

import android.annotation.SuppressLint;
import android.util.Log;

import com.amazonaws.cognito.sync.demo.Cognito;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is used to communicate with the sample Cognito developer
 * authentication application sample.
 */
public class ServerApiClient {

    private static final String LOG_TAG = "ServerApiClient";

    /**
     * The host for the sample Cognito developer authentication application.
     */
    private final URL host;

    /**
     * The appName declared by the sample Cognito developer authentication
     * application.
     */
    private final String appName;

    public ServerApiClient(URL host, String appName) {
        this.host = host;
        this.appName = appName.toLowerCase();
    }

    /**
     * A function to send request to the sample Cognito developer authentication
     * application
     *
     * @param request
     * @param reponseHandler
     * @return
     */
    public static Response sendRequest(Request request, ResponseHandler reponseHandler) {
        int responseCode = 0;
        String responseBody = null;
        String requestUrl = null;
        try {
            requestUrl = request.buildRequestUrl();

            Log.i(LOG_TAG, "Sending Request : [" + requestUrl + "]");

            URL url = new URL(requestUrl);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();

            responseCode = connection.getResponseCode();
            responseBody = getResponse(connection);
            Log.i(LOG_TAG, "Response : [" + responseBody + "]");

            return reponseHandler.handleResponse(responseCode, responseBody);
        } catch (IOException exception) {
            Log.w(LOG_TAG, exception);
            if (exception.getMessage().equals(
                    "Received authentication challenge is null")) {
                return reponseHandler.handleResponse(401,
                        "Unauthorized token request");
            } else {
                return reponseHandler.handleResponse(404,
                        "Unable to reach resource at [" + requestUrl + "]");
            }
        } catch (Exception exception) {
            Log.w(LOG_TAG, exception);
            return reponseHandler.handleResponse(responseCode, responseBody);
        }
    }

    /**
     * Function to get the response from sample Cognito developer authentication
     * application.
     *
     * @param connection
     * @return
     */
    private static String getResponse(HttpURLConnection connection) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
        InputStream inputStream = null;
        try {
            baos = new ByteArrayOutputStream(1024);
            int length = 0;
            byte[] buffer = new byte[1024];

            if (connection.getResponseCode() == 200) {
                inputStream = connection.getInputStream();
            } else {
                inputStream = connection.getErrorStream();
            }

            while ((length = inputStream.read(buffer)) != -1) {
                baos.write(buffer, 0, length);
            }

            return baos.toString();
        } catch (Exception exception) {
            Log.w(LOG_TAG, exception);
            return "Internal Server Error";
        } finally {
            try {
                baos.close();
            } catch (Exception exception) {
                Log.w(LOG_TAG, exception);
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

        Request getTokenRequest = new GetTokenRequest(this.host, endpoint, uid, key, logins, identityId);

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
        LoginRequest loginRequest = new LoginRequest(this.host, this.appName, uid, username, password);
        ResponseHandler handler = new LoginResponseHandler(loginRequest.getDecryptionKey());

        Response response = this.processRequest(loginRequest, handler);

        return response;
    }

    /**
     * Process Request
     */
    @SuppressLint("LongLogTag")
    private Response processRequest(Request request, ResponseHandler handler) {
        Response response = null;
        int retries = 2;
        do {
            response = sendRequest(
                    request, handler);
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
