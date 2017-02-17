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

import okhttp3.HttpUrl;

/**
 * This class is used to construct the Login request for communication with
 * sample Cognito developer authentication.
 */
public class LoginRequestData extends RequestData {

    private final String baseUrl;
    private final String uid;
    private final String username;
    private final String password;
    private final String appName;

    private final String decryptionKey;

    public LoginRequestData(String baseUrl, String appName,
                            String uid, String username, String password,
                            String decryptionKey) {
        this.baseUrl = baseUrl;
        this.appName = appName;
        this.uid = uid;
        this.username = username;
        this.password = password;

        this.decryptionKey = decryptionKey;
    }

    public String getDecryptionKey() {
        return decryptionKey;
    }

    @Override
    public String buildRequestUrl() {
        String timestamp = Utilities.getTimestamp();
        String signature = Utilities.getSignature(timestamp, decryptionKey);
        return HttpUrl.parse(baseUrl)
                .newBuilder()
                .addPathSegment("login")
                .addQueryParameter("uid", uid)
                .addQueryParameter("username", username)
                .addQueryParameter("timestamp", timestamp)
                .addQueryParameter("signature", signature)
                .toString();
    }

    private String computeDecryptionKey() {
        String salt = username + appName + HttpUrl.parse(baseUrl).host();
        return Utilities.getSignature(salt, password);
    }
}
