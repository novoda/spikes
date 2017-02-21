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

package com.amazonaws.cognito.sync.demo.client.firebase;

import com.amazonaws.cognito.sync.demo.client.ResponseHandler;

import java.io.IOException;

import okhttp3.Response;

public class FirebaseTokenResponseHandler implements ResponseHandler<FirebaseTokenResponseData> {

    @Override
    public FirebaseTokenResponseData handleResponse(Response response) throws IOException {
        String token = response.body().string();
        return new FirebaseTokenResponseData(token);
    }

}
