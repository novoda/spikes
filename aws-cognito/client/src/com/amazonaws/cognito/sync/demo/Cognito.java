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

package com.amazonaws.cognito.sync.demo;

import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Log;

import com.amazonaws.auth.AWSAbstractCognitoIdentityProvider;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.cognito.sync.devauth.client.ServerApiClient;
import com.amazonaws.mobileconnectors.cognito.CognitoSyncManager;
import com.amazonaws.regions.Regions;

import java.net.MalformedURLException;
import java.net.URL;

public enum Cognito {

    INSTANCE;

    private static final String IDENTITY_POOL_ID = BuildConfig.IDENTITY_POOL;
    private static final Regions REGION = Regions.fromName(BuildConfig.REGION);

    private CognitoSyncManager syncManager;
    private CognitoCachingCredentialsProvider credentialsProvider = null;
    private ServerApiClient serverApiClient;
    private Identifiers identifiers;

    public void init(Context context) {
        serverApiClient = createServerApiClient(context);
        syncManager = createSyncManager(context, serverApiClient);
        identifiers = new Identifiers(PreferenceManager.getDefaultSharedPreferences(context));
    }

    public Identifiers getIdentifiers() {
        return identifiers;
    }


    private CognitoSyncManager createSyncManager(Context context, ServerApiClient serverApiClient) {
        AWSAbstractCognitoIdentityProvider identityProvider = new ServerCognitoIdentityProvider(serverApiClient, null, IDENTITY_POOL_ID, context, REGION);
        credentialsProvider = new CognitoCachingCredentialsProvider(context, identityProvider, REGION);
        return new CognitoSyncManager(context, REGION, credentialsProvider);
    }

    private ServerApiClient createServerApiClient(Context context) {
        try {
            URL host = new URL(BuildConfig.AUTHENTICATION_ENDPOINT);
            return new ServerApiClient(host, "AWSCognitoDeveloperAuthenticationSample");
        } catch (MalformedURLException e) {
            Log.e("DeveloperAuthentication", "Developer Authentication Endpoint is not a valid URL!", e);
            throw new RuntimeException(e);
        }
    }

    public CognitoSyncManager syncManager() {
        return syncManager;
    }

    public CognitoCachingCredentialsProvider credentialsProvider() {
        return credentialsProvider;
    }

    public ServerApiClient serverApiClient() {
        return serverApiClient;
    }
}
