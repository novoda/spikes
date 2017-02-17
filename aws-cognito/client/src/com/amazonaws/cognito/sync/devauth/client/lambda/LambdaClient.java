package com.amazonaws.cognito.sync.devauth.client.lambda;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.cognito.sync.demo.Cognito;
import com.amazonaws.cognito.sync.demo.ServerCognitoIdentityProvider;
import com.amazonaws.mobileconnectors.lambdainvoker.LambdaInvokerFactory;
import com.amazonaws.regions.Regions;

public class LambdaClient {

    public void testAuth(final Context context) {
        ServerCognitoIdentityProvider identityProvider = (ServerCognitoIdentityProvider) Cognito.INSTANCE.credentialsProvider().getIdentityProvider();
        // Create an instance of CognitoCachingCredentialsProvider
        CognitoCachingCredentialsProvider cognitoProvider = new CognitoCachingCredentialsProvider(context, identityProvider, Regions.EU_WEST_1);

        // Create LambdaInvokerFactory, to be used to instantiate the Lambda proxy.
        LambdaInvokerFactory factory = new LambdaInvokerFactory(context, Regions.EU_WEST_1, cognitoProvider);

        // Create the Lambda proxy object with a default Json data binder.
        // You can provide your own data binder by implementing
        // LambdaDataBinder.
        final LambdaInterface lambdaInterface = factory.build(LambdaInterface.class);
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                try {
                    return lambdaInterface.CognitoAuthTest();
                } catch (Exception lfe) {
                    Log.e("Tag", "Failed to invoke", lfe);
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String result) {
                if (result == null) {
                    Toast.makeText(context, "Failed to invoke lambda :( Are you sure you are logged in? ", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(context, result, Toast.LENGTH_LONG).show();
                }
            }
        }.execute();
    }

}
