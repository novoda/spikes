package com.amazonaws.cognito.sync.devauth.client.lambda;

import com.amazonaws.mobileconnectors.lambdainvoker.LambdaFunction;

public interface LambdaInterface {

    /**
     * Invoke the Lambda function "LoginLambda".
     * The function name is the method name.
     */
    @LambdaFunction
    LambdaClient.Response LoginLambda(LambdaClient.Request request);

}
