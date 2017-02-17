package com.amazonaws.cognito.sync.demo.client.cognito;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.mobileconnectors.lambdainvoker.LambdaFunction;
import com.amazonaws.mobileconnectors.lambdainvoker.LambdaInvokerFactory;
import com.amazonaws.regions.Regions;

public class AccessLambdaTask extends AsyncTask<Void, Void, String> {

    private final LambdaInterface lambdaAccess;
    private final Context context;
    private final Regions region;

    public AccessLambdaTask(Context context, AWSCredentialsProvider credentialsProvider, Regions region) {
        this.context = context;
        this.region = region;
        lambdaAccess = createLambdaAccess(context, credentialsProvider, region);
    }

    private static LambdaInterface createLambdaAccess(final Context context, AWSCredentialsProvider credentialsProvider, Regions region) {
        LambdaInvokerFactory factory = new LambdaInvokerFactory(context, region, credentialsProvider);
        return factory.build(LambdaInterface.class);
    }

    @Override
    protected String doInBackground(final Void... params) {
        try {
            return lambdaAccess.readHiddenText();
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

    interface LambdaInterface {

        @LambdaFunction(functionName = "CognitoAuthTest")
        String readHiddenText();

    }
}
