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

import android.util.Log;

import com.amazonaws.cognito.sync.demo.client.ResponseData;
import com.amazonaws.cognito.sync.demo.client.ServerApiClient;

import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

public class FirebaseTokenTask implements ObservableOnSubscribe<String> {

    private final ServerApiClient apiClient;

    public FirebaseTokenTask(ServerApiClient apiClient) {
        this.apiClient = apiClient;
    }

    @Override
    public void subscribe(final ObservableEmitter<String> emitter) throws Exception {
        ResponseData responseData = apiClient.getFirebaseToken();
        if (responseData.requestWasSuccessful()) {
            String token = responseData.getResponseMessage();
            emitter.onNext(token);
            emitter.onComplete();
        } else {
            Log.e("FirebaseTokenTask", responseData.getResponseMessage());
            emitter.onError(new RuntimeException("Can't fetch firebase token, did you log in to the custon server first?"));
        }
    }
}
