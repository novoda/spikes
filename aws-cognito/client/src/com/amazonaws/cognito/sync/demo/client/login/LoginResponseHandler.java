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

package com.amazonaws.cognito.sync.demo.client.login;

import com.amazonaws.cognito.sync.demo.client.AESEncryption;
import com.amazonaws.cognito.sync.demo.client.ResponseHandler;
import com.amazonaws.cognito.sync.demo.client.Utilities;

import java.io.IOException;

import okhttp3.Response;

public class LoginResponseHandler implements ResponseHandler<LoginResponseData> {
    private final String decryptionKey;

    public LoginResponseHandler(final String decryptionKey) {
        this.decryptionKey = decryptionKey;
    }

    public LoginResponseData handleResponse(Response response) throws IOException {
        try {
            String json = AESEncryption.unwrap(response.body().string(), this.decryptionKey.substring(0, 32));
            return new LoginResponseData(Utilities.extractElement(json, "key"));
        } catch (Exception e) {
            throw new IOException(e);
        }
    }
}
