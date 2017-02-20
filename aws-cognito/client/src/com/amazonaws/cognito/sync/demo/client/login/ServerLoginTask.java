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

import android.util.Log;

import com.amazonaws.cognito.sync.demo.Identifiers;
import com.amazonaws.cognito.sync.demo.client.ServerApiClient;

import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

public class ServerLoginTask implements ObservableOnSubscribe<String> {

    private final ServerApiClient apiClient;
    private final Identifiers identifiers;
    private final LoginCredentials loginCredentials;

    public ServerLoginTask(ServerApiClient apiClient, Identifiers identifiers, LoginCredentials loginCredentials) {
        this.apiClient = apiClient;
        this.identifiers = identifiers;
        this.loginCredentials = loginCredentials;
    }

    @Override
    public void subscribe(ObservableEmitter<String> emitter) throws Exception {
        String username = loginCredentials.getUsername();
        LoginResponseData responseData = apiClient.login(username, loginCredentials.getPassword());
        if (responseData.requestWasSuccessful()) {
            String deviceKey = responseData.getKey();
            identifiers.registerUser(username, deviceKey);
            emitter.onNext("User \"" + username + "\" logged in!");
            emitter.onComplete();
        } else {
            Log.e("ServerLogin", responseData.getResponseMessage());
            emitter.onError(new RuntimeException("Can't log in to the server, is that the right username / password?"));
        }
    }

}
