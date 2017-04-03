package com.amazonaws.cognito.sync.demo.client.cognito;

import android.content.Context;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.mobileconnectors.lambdainvoker.LambdaFunction;
import com.amazonaws.mobileconnectors.lambdainvoker.LambdaInvokerFactory;
import com.amazonaws.regions.Regions;

import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

public class CognitoResourceAccessTask implements ObservableOnSubscribe<String> {

    private final LambdaInterface lambdaAccess;

    public CognitoResourceAccessTask(Context context, AWSCredentialsProvider credentialsProvider, Regions region) {
        lambdaAccess = createLambdaAccess(context, credentialsProvider, region);
    }

    private static LambdaInterface createLambdaAccess(Context context, AWSCredentialsProvider credentialsProvider, Regions region) {
        LambdaInvokerFactory factory = new LambdaInvokerFactory(context, region, credentialsProvider);
        return factory.build(LambdaInterface.class);
    }

    @Override
    public void subscribe(ObservableEmitter<String> emitter) throws Exception {
        String result = lambdaAccess.readHiddenText();
        emitter.onNext(result);
        emitter.onComplete();
    }

    interface LambdaInterface {

        @LambdaFunction(functionName = "CognitoAuthTest")
        String readHiddenText();

    }
}
