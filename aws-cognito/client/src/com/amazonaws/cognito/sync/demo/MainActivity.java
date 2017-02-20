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
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.amazonaws.cognito.sync.demo.client.ServerApiClient;
import com.amazonaws.cognito.sync.demo.client.cognito.Cognito;
import com.amazonaws.cognito.sync.demo.client.cognito.CognitoResourceAccessTask;
import com.amazonaws.cognito.sync.demo.client.cognito.CognitoTokenTask;
import com.amazonaws.cognito.sync.demo.client.firebase.FirebaseLogInTask;
import com.amazonaws.cognito.sync.demo.client.firebase.FirebaseResourceAccessTask;
import com.amazonaws.cognito.sync.demo.client.firebase.FirebaseTokenTask;
import com.amazonaws.cognito.sync.demo.client.login.LoginCredentials;
import com.amazonaws.cognito.sync.demo.client.login.ServerLoginTask;
import com.amazonaws.regions.Regions;
import com.google.firebase.auth.FirebaseAuth;

import io.reactivex.Completable;
import io.reactivex.CompletableSource;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends Activity {

    private ServerApiClient apiClient;
    private Identifiers identifiers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        apiClient = ((AuthApplication) getApplication()).getApiClient();
        identifiers = ((AuthApplication) getApplication()).getIdentifiers();

        Button btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(MainActivity.this);
                dialog.setContentView(R.layout.login_dialog);
                dialog.setTitle("Sample developer dialog");
                final TextView txtUsername = (TextView) dialog.findViewById(R.id.txtUsername);
                txtUsername.setHint("Username");
                final TextView txtPassword = (TextView) dialog.findViewById(R.id.txtPassword);
                txtPassword.setHint("Password");
                Button btnOk = (Button) dialog.findViewById(R.id.btnLogin);
                Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);

                btnCancel.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                btnOk.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String username = txtUsername.getText().toString();
                        String password = txtPassword.getText().toString();
                        if (username.isEmpty() || password.isEmpty()) {
                            new AlertDialog.Builder(MainActivity.this)
                                    .setTitle("Login error")
                                    .setMessage("Username or password cannot be empty!!")
                                    .show();
                        } else {
                            login(username, password);
                        }
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });

        findViewById(R.id.btnAccessFirebaseResource).setOnClickListener(
                new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        fetchFirebaseToken()
                                .flatMapCompletable(new Function<String, CompletableSource>() {
                                    @Override
                                    public CompletableSource apply(@NonNull String token) throws Exception {
                                        return signInToFirebase(token);
                                    }
                                })
                                .andThen(accessFirebaseResource())
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new ToastObserver(MainActivity.this));
                    }
                });

        findViewById(R.id.btnAccessResourceCognito).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                fetchCognitoToken()
                        .andThen(accessCognitoResource())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new ToastObserver(MainActivity.this));
            }
        });

        findViewById(R.id.btnWipedata).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                wipeData();
            }
        });
    }

    private Completable fetchCognitoToken() {
        return Completable.create(new CognitoTokenTask(Cognito.INSTANCE.credentialsProvider(), identifiers.getUserName()));
    }

    private Observable<String> accessCognitoResource() {
        Regions region = Regions.fromName(BuildConfig.REGION);
        return Observable.create(new CognitoResourceAccessTask(getApplicationContext(), Cognito.INSTANCE.credentialsProvider(), region));
    }

    private Observable<String> fetchFirebaseToken() {
        return Observable.create(new FirebaseTokenTask(apiClient));
    }

    private Completable signInToFirebase(String token) {
        return Completable.create(new FirebaseLogInTask(token));
    }

    private Observable<String> accessFirebaseResource() {
        return Observable.create(new FirebaseResourceAccessTask());
    }

    private void login(String username, String password) {
        Cognito.INSTANCE.credentialsProvider().clearCredentials();
        LoginCredentials loginCredentials = new LoginCredentials(username, password);
        new ServerLoginTask(this, apiClient, identifiers).execute(loginCredentials);
    }

    private void wipeData() {
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Wipe data?")
                .setMessage("This will log off your current session and wipe all user data. Any data not synchronized will be lost.")
                .setPositiveButton("Yes", wipeListener())
                .setNegativeButton("No", cancelListener())
                .show();
    }

    private DialogInterface.OnClickListener cancelListener() {
        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        };
    }

    private DialogInterface.OnClickListener wipeListener() {
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
