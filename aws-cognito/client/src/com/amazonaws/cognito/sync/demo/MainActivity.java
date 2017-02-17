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
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.cognito.sync.devauth.client.SharedPreferencesWrapper;
import com.amazonaws.cognito.sync.devauth.client.lambda.LambdaClient;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";

    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

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

        findViewById(R.id.btnFirebaseToken).setOnClickListener(
                new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        fetchFirebaseToken();
                    }
                });

        findViewById(R.id.btnAccessFirebaseResource).setOnClickListener(
                new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        accessFirebaseResource();
                    }
                });

        findViewById(R.id.btnCognitoToken).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                fetchCognitoToken();
            }
        });

        findViewById(R.id.btnResourceCognito).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                accessCognitoResource();
            }
        });

        findViewById(R.id.btnWipedata).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                wipeData();
            }
        });
    }

    private void fetchCognitoToken() {
        new CognitoTokenTask().execute(SharedPreferencesWrapper.getUserName(preferences));
    }

    private void accessFirebaseResource() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("test_reading");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Toast.makeText(MainActivity.this, "Accessed a restricted resource that says: " + dataSnapshot.getValue(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, databaseError.getMessage());
                Toast.makeText(MainActivity.this, "Can't read resource", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchFirebaseToken() {
        String uid = SharedPreferencesWrapper.getUidForDevice(preferences);
        new FirebaseTokenTask(this, Cognito.INSTANCE.serverApiClient()).execute(uid);
    }

    private void login(String username, String password) {
        Cognito.INSTANCE.credentialsProvider().clearCredentials();
        LoginCredentials loginCredentials = new LoginCredentials(username, password);
        new ServerLoginTask(this, Cognito.INSTANCE.serverApiClient(), preferences).execute(loginCredentials);
    }

    private void accessCognitoResource() {
        new LambdaClient().testAuth(this);
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
                SharedPreferencesWrapper.wipe(PreferenceManager.getDefaultSharedPreferences(MainActivity.this));
            }

        };
    }

}
