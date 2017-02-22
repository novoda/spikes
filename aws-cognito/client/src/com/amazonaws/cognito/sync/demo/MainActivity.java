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

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.cognito.sync.demo.client.cognito.Cognito;
import com.amazonaws.cognito.sync.demo.client.cognito.CognitoResourceAccessTask;
import com.amazonaws.cognito.sync.demo.client.cognito.CognitoTokenTask;
import com.amazonaws.cognito.sync.demo.client.firebase.FirebaseLogInTask;
import com.amazonaws.cognito.sync.demo.client.firebase.FirebaseResourceAccessTask;
import com.amazonaws.cognito.sync.demo.client.firebase.FirebaseTokenResponseData;
import com.amazonaws.cognito.sync.demo.client.server.AndroidExecutionStrategy;
import com.amazonaws.cognito.sync.demo.client.server.LoginResponseData;
import com.amazonaws.cognito.sync.demo.client.server.ServerApiClient;
import com.amazonaws.regions.Regions;
import com.google.firebase.auth.FirebaseAuth;

import io.reactivex.Completable;
import io.reactivex.CompletableSource;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

public class MainActivity extends Activity {

    private ServerApiClient apiClient;
    private Identifiers identifiers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        apiClient = ((AuthApplication) getApplication()).getApiClient();
        identifiers = ((AuthApplication) getApplication()).getIdentifiers();

        findViewById(R.id.btnLogin).setOnClickListener(showLoginDialog());
        findViewById(R.id.btnAccessResourceCognito).setOnClickListener(triggerAccessToCognitoResource());
        findViewById(R.id.btnAccessFirebaseResource).setOnClickListener(triggerAccessToFirebaseResource());
        findViewById(R.id.btnWipedata).setOnClickListener(showWipeConfirmationDialog());
    }

    private OnClickListener showLoginDialog() {
        return new OnClickListener() {
            @Override
            public void onClick(View view) {
                View layout = getLayoutInflater().inflate(R.layout.login_dialog, null);
                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                dialog.setView(layout);
                dialog.setTitle("Sample developer login");
                String username = extractTextFrom(layout, R.id.txtUsername);
                String password = extractTextFrom(layout, R.id.txtPassword);
                dialog.setPositiveButton("Login", triggerLogin(username, password));
                dialog.setNegativeButton("Cancel", null);
                dialog.create().show();
            }
        };
    }

    private String extractTextFrom(View view, int textViewResourceId) {
        return ((TextView) view.findViewById(textViewResourceId)).getText().toString();
    }

    private DialogInterface.OnClickListener triggerLogin(final String username, final String password) {
        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(MainActivity.this, "username or password cannot be empty!", Toast.LENGTH_LONG).show();
                } else {
                    login(username, password);
                }
                dialog.dismiss();
            }
        };
    }

    private void login(final String username, String password) {
        Cognito.INSTANCE.credentialsProvider().clearCredentials();
        apiClient.login(username, password, identifiers.getUidForDevice())
                .doOnNext(saveUserInformations(username))
                .map(new Function<LoginResponseData, String>() {
                    @Override
                    public String apply(@NonNull LoginResponseData login) throws Exception {
                        return "User " + username + " logged in!";
                    }
                })
                .compose(new AndroidExecutionStrategy<String>())
                .subscribe(new ToastObserver(this));
    }

    private Consumer<LoginResponseData> saveUserInformations(final String username) {
        return new Consumer<LoginResponseData>() {
            @Override
            public void accept(@NonNull LoginResponseData login) throws Exception {
                identifiers.registerUser(username, login.getKey());
            }
        };
    }

    private OnClickListener triggerAccessToCognitoResource() {
        return new OnClickListener() {
            @Override
            public void onClick(View view) {
                fetchCognitoToken()
                        .doOnNext(saveCognitoToken())
                        .flatMap(accessCognitoResource())
                        .compose(new AndroidExecutionStrategy<String>())
                        .subscribe(new ToastObserver(MainActivity.this));
            }
        };
    }

    private Consumer<String> saveCognitoToken() {
        return new Consumer<String>() {
            @Override
            public void accept(@NonNull String token) throws Exception {
                identifiers.registerCognitoToken(token);
            }
        };
    }

    private Observable<String> fetchCognitoToken() {
        return Observable.create(new CognitoTokenTask(Cognito.INSTANCE.credentialsProvider(), identifiers.getUserName()));
    }

    private Function<String, ObservableSource<String>> accessCognitoResource() {
        return new Function<String, ObservableSource<String>>() {
            @Override
            public ObservableSource<String> apply(@NonNull String ignore) throws Exception {
                CognitoResourceAccessTask source = new CognitoResourceAccessTask(getApplicationContext(), Cognito.INSTANCE.credentialsProvider(), Regions.fromName(BuildConfig.REGION));
                return Observable.create(source);
            }
        };
    }

    private OnClickListener triggerAccessToFirebaseResource() {
        return new OnClickListener() {
            @Override
            public void onClick(View view) {
                fetchFirebaseToken()
                        .flatMapCompletable(signInToFirebase())
                        .andThen(accessFirebaseResource())
                        .compose(new AndroidExecutionStrategy<String>())
                        .subscribe(new ToastObserver(MainActivity.this));
            }
        };
    }

    private Observable<String> fetchFirebaseToken() {
        return apiClient.getFirebaseToken(identifiers.getFirebaseToken(), identifiers.getKeyForDevice())
                .map(toTokenOnly())
                .doOnNext(saveFirebaseToken());
    }

    private Function<FirebaseTokenResponseData, String> toTokenOnly() {
        return new Function<FirebaseTokenResponseData, String>() {
            @Override
            public String apply(@NonNull FirebaseTokenResponseData response) throws Exception {
                return response.getToken();
            }
        };
    }

    private Consumer<String> saveFirebaseToken() {
        return new Consumer<String>() {
            @Override
            public void accept(@NonNull String token) throws Exception {
                identifiers.registerFirebaseToken(token);
            }
        };
    }

    private Function<String, CompletableSource> signInToFirebase() {
        return new Function<String, CompletableSource>() {
            @Override
            public CompletableSource apply(@NonNull String token) throws Exception {
                return Completable.create(new FirebaseLogInTask(token));
            }
        };
    }

    private Observable<String> accessFirebaseResource() {
        return Observable.create(new FirebaseResourceAccessTask());
    }

    private OnClickListener showWipeConfirmationDialog() {
        return new OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Wipe data?")
                        .setMessage("This will log off your current session and wipe all user data. Any data not synchronized will be lost.")
                        .setPositiveButton("Yes", triggerDataWipe())
                        .setNegativeButton("No", null)
                        .show();
            }
        };
    }

    private DialogInterface.OnClickListener triggerDataWipe() {
        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Cognito.INSTANCE.syncManager().wipeData();
                Cognito.INSTANCE.credentialsProvider().clearCredentials();
                FirebaseAuth.getInstance().signOut();
                identifiers.wipe();
            }

        };
    }

}
