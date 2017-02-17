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

import android.support.annotation.NonNull;

import java.util.Map;

import okhttp3.HttpUrl;

/**
 * A class used to construct the GetToken request to the sample Cognito
 * developer authentication application.
 */
public class GetTokenRequestData extends RequestData {

    private final String host;
    private final String endpoint;
    private final String uid;
    private final String key;
    private final Map<String, String> logins;
    private final String identityId;

    public GetTokenRequestData(final String host, final String endpoint, final String uid, final String key,
                               Map<String, String> logins, String identityId) {
        this.host = host;
        this.endpoint = endpoint;
        this.uid = uid;
        this.key = key;
        this.logins = logins;
        this.identityId = identityId;
    }

    @Override
    public String buildRequestUrl() {
        String timestamp = Utilities.getTimestamp();

        HttpUrl.Builder builder = HttpUrl.parse(host)
                .newBuilder()
                .addPathSegment(endpoint)
                .addQueryParameter("uid", uid)
                .addQueryParameter("timestamp", timestamp)
                .addQueryParameter("identityId", identityId)
                .addQueryParameter("signature", signature(timestamp));

        return appendLogins(builder).toString();
    }

    private HttpUrl.Builder appendLogins(final HttpUrl.Builder builder) {
        int counter = 1;
        for (Map.Entry<String, String> entry : logins.entrySet()) {
            builder.addQueryParameter("provider" + counter, entry.getKey());
            builder.addQueryParameter("token" + counter, entry.getValue());
            counter++;
        }
        return builder;
    }

    private String signature(final String timestamp) {
        StringBuilder loginString = loginString();
        if (identityId != null) {
            return Utilities.getSignature(timestamp + loginString + identityId, key);
        } else {
            return Utilities.getSignature(timestamp + loginString, key);
        }
    }

    @NonNull
    private StringBuilder loginString() {
        StringBuilder loginString = new StringBuilder();
        for (Map.Entry<String, String> entry : logins.entrySet()) {
            loginString.append(entry.getKey()).append(entry.getValue());
        }
        return loginString;
    }

}
