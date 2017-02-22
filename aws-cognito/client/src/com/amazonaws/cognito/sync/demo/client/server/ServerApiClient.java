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

package com.amazonaws.cognito.sync.demo.client.server;

import com.amazonaws.cognito.sync.demo.Identifiers;
import com.amazonaws.cognito.sync.demo.client.cognito.CognitoTokenRequestData;
import com.amazonaws.cognito.sync.demo.client.cognito.CognitoTokenResponseData;
import com.amazonaws.cognito.sync.demo.client.cognito.CognitoTokenResponseHandler;
import com.amazonaws.cognito.sync.demo.client.firebase.FirebaseTokenResponseData;
import com.amazonaws.cognito.sync.demo.client.firebase.FirebaseTokenResponseHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.BiPredicate;
import io.reactivex.functions.Function;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ServerApiClient {

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

    public Observable<CognitoTokenResponseData> getCognitoToken(Map<String, String> logins, String identityId) {
        String key = identifiers.getKeyForDevice();
        String token = identifiers.getCognitoToken();
        if (token == null) {
            return getToken(logins, identityId, "gettoken", new CognitoTokenResponseHandler(key), "cognito");
        }
        return Observable.just(new CognitoTokenResponseData(identityId, token));
    }

    public Observable<FirebaseTokenResponseData> getFirebaseToken() {
        String firebaseToken = identifiers.getFirebaseToken();
        String key = identifiers.getKeyForDevice();
        if (firebaseToken == null) {
            return getToken(new HashMap<String, String>(), null, "getfirebasetoken", new FirebaseTokenResponseHandler(key), "firebase");
        }
        return Observable.just(new FirebaseTokenResponseData(firebaseToken));
    }

    private <T> Observable<T> getToken(Map<String, String> logins, String identityId, String endpoint, ResponseHandler<T> responseHandler, String tokenType) {
        String uid = identifiers.getUidForDevice();
        String key = identifiers.getKeyForDevice();
        RequestData getTokenRequestData = new CognitoTokenRequestData(baseUrl, endpoint, uid, key, logins, identityId);
        ErrorToMessageConverter messageConverter = new ErrorToMessageConverter("Can't fetch " + tokenType + " token, did you log in to the server before?");
        return execute(getTokenRequestData, responseHandler, new HttpErrorInterceptor(messageConverter));
    }

    public Observable<LoginResponseData> login(String username, String password) {
        String uid = identifiers.getUidForDevice();
        String decryptionKey = computeDecryptionKey(username, password, baseUrl);
        LoginRequestData loginRequest = new LoginRequestData(baseUrl, uid, username, decryptionKey);
        ResponseHandler<LoginResponseData> handler = new LoginResponseHandler(decryptionKey);
        ErrorToMessageConverter messageConverter = new ErrorToMessageConverter("Can't login user: " + username);
        return execute(loginRequest, handler, new HttpErrorInterceptor(messageConverter));
    }

    private String computeDecryptionKey(String username, String password, String baseUrl) {
        String salt = username + appName + HttpUrl.parse(baseUrl).host();
        return Utilities.getSignature(salt, password);
    }

    private <T> Observable<T> execute(RequestData requestData, ResponseHandler<T> handler, HttpErrorInterceptor errorInterceptor) {
        return tryExecute(requestData, handler, errorInterceptor)
                .retry(new BiPredicate<Integer, Throwable>() {
                    @Override
                    public boolean test(@NonNull Integer attempts, @NonNull Throwable throwable) throws Exception {
                        return attempts < 2;
                    }
                });
    }

    private <T> Observable<T> tryExecute(RequestData requestData, final ResponseHandler<T> handler, HttpErrorInterceptor errorInterceptor) {
        Request request = new Request.Builder().url(requestData.buildRequestUrl()).build();
        return send(request)
                .flatMap(errorInterceptor)
                .map(new Function<Response, T>() {
                    @Override
                    public T apply(@NonNull Response response) throws Exception {
                        return handler.handleResponse(response.body().string());
                    }
                });
    }

    private Observable<Response> send(final Request request) {
        return Observable.create(new ObservableOnSubscribe<Response>() {
            @Override
            public void subscribe(ObservableEmitter<Response> emitter) throws Exception {
                try {
                    Response response = client.newCall(request).execute();
                    emitter.onNext(response);
                    emitter.onComplete();
                } catch (IOException e) {
                    emitter.onError(e);
                }
            }
        });
    }

}
