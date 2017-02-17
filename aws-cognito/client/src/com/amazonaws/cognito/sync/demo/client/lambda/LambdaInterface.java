package com.amazonaws.cognito.sync.demo.client.lambda;

import com.amazonaws.mobileconnectors.lambdainvoker.LambdaFunction;

public interface LambdaInterface {

    @LambdaFunction(functionName = "CognitoAuthTest")
    String readHiddenText();

}
