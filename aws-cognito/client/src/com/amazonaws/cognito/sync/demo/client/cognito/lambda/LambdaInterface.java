package com.amazonaws.cognito.sync.demo.client.cognito.lambda;

import com.amazonaws.mobileconnectors.lambdainvoker.LambdaFunction;

public interface LambdaInterface {

    @LambdaFunction(functionName = "CognitoAuthTest")
    String readHiddenText();

}
