/**
 * Copyright 2010-2014 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package com.amazonaws.cognito.sync.demo.client.cognito;

import com.amazonaws.cognito.sync.demo.client.AESEncryption;
import com.amazonaws.cognito.sync.demo.client.ResponseHandler;
import com.amazonaws.cognito.sync.demo.client.Utilities;

import java.io.IOException;

import okhttp3.Response;

public class CognitoTokenResponseHandler implements ResponseHandler<CognitoTokenResponseData> {

    private final String key;

    public CognitoTokenResponseHandler(final String key) {
        this.key = key;
    }

    @Override
    public CognitoTokenResponseData handleResponse(Response response) throws IOException {
        if (response.isSuccessful()) {
            try {
                String json = AESEncryption.unwrap(response.body().string(), this.key);
                String identityId = Utilities.extractElement(json, "identityId");
                String token = Utilities.extractElement(json, "token");
                return new CognitoTokenResponseData(identityId, token);
            } catch (Exception exception) {
                throw new IOException(exception);
            }
        } else {
            return new CognitoTokenResponseData();
        }
    }

}
