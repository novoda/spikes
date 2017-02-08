package com.amazonaws.cognito.sync.devauth.client.lambda;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.cognito.sync.demo.BuildConfig;
import com.amazonaws.cognito.sync.devauth.client.AmazonCognitoSampleDeveloperAuthenticationClient;
import com.amazonaws.cognito.sync.devauth.client.AmazonSharedPreferencesWrapper;
import com.amazonaws.cognito.sync.devauth.client.Utilities;
import com.amazonaws.mobileconnectors.lambdainvoker.LambdaFunctionException;
import com.amazonaws.mobileconnectors.lambdainvoker.LambdaInvokerFactory;
import com.amazonaws.regions.Regions;

import java.net.MalformedURLException;
import java.net.URL;

public class LambdaClient {

    public void login(final Context context, String username, String password) {

        // Create an instance of CognitoCachingCredentialsProvider
        CognitoCachingCredentialsProvider cognitoProvider = new CognitoCachingCredentialsProvider(context, BuildConfig.IDENTITY_POOL, Regions.EU_WEST_1);

        // Create LambdaInvokerFactory, to be used to instantiate the Lambda proxy.
        LambdaInvokerFactory factory = new LambdaInvokerFactory(context, Regions.EU_WEST_1, cognitoProvider);

        // Create the Lambda proxy object with a default Json data binder.
        // You can provide your own data binder by implementing
        // LambdaDataBinder.
        final LambdaInterface lambdaInterface = factory.build(LambdaInterface.class);

        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String uid = AmazonSharedPreferencesWrapper.getUidForDevice(sharedPreferences);
        if (uid == null) {
            uid = AmazonCognitoSampleDeveloperAuthenticationClient.generateRandomString();

            String timestamp = Utilities.getTimestamp();
            String host;
            try {
                host = new URL(BuildConfig.AUTHENTICATION_ENDPOINT).getHost();
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
            String appname = "AWSCognitoDeveloperAuthenticationSample".toLowerCase();
            String decryptionKey = computeDecryptionKey(username, appname, host, password);
            Log.e("decryption", "lambda: " + decryptionKey);
            String signature = Utilities.getSignature(timestamp, decryptionKey);

            Request request = new Request(username, timestamp, signature, uid);
            // The Lambda function invocation results in a network call.
            // Make sure it is not called from the main thread.
            new AsyncTask<Request, Void, Response>() {

                private Request request;

                @Override
                protected Response doInBackground(Request... params) {
                    // invoke "echo" method. In case it fails, it will throw a
                    // LambdaFunctionException.
                    try {
                        request = params[0];
                        return lambdaInterface.LoginLambda(request);
                    } catch (LambdaFunctionException lfe) {
                        Log.e("Tag", "Failed to invoke", lfe);
                        return null;
                    }
                }

                @Override
                protected void onPostExecute(Response result) {
                    if (result.httpResponseCode == 200) {
                        Toast.makeText(context, "worked! " + result.key, Toast.LENGTH_LONG).show();
                        AmazonSharedPreferencesWrapper.registerDeviceId(sharedPreferences, request.uid, result.key());
                    } else {
                        Toast.makeText(context, "failed!", Toast.LENGTH_LONG).show();
                    }

                }
            }.execute(request);
        } else {
            Toast.makeText(context, "Device already registered!", Toast.LENGTH_LONG).show();
        }
    }

    protected String computeDecryptionKey(String username, String appname, String host, String password) {
        try {
            String salt = username + appname + host;
            return Utilities.getSignature(salt, password);
        } catch (Exception exception) {
            return null;
        }
    }

    static class Response {

        private int httpResponseCode;
        private String key;

        Response(int httpResponseCode, String key) {
            this.httpResponseCode = httpResponseCode;
            this.key = key;
        }

        public String key() {
            return key;
        }
    }

    static class Request {

        private String username;
        private String timestamp;
        private String signature;
        private String uid;

        public Request(String username, String timestamp, String signature, String uid) {
            this.username = username;
            this.timestamp = timestamp;
            this.signature = signature;
            this.uid = uid;
        }
    }
}
