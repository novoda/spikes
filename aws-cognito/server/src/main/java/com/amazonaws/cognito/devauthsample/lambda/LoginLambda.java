/*
 * Copyright 2010-2015 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package com.amazonaws.cognito.devauthsample.lambda;

import com.amazonaws.cognito.devauthsample.exception.DataAccessException;
import com.amazonaws.cognito.devauthsample.exception.UnauthorizedException;
import com.amazonaws.cognito.devauthsample.identity.AWSCognitoDeveloperAuthenticationSample;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import javax.servlet.http.HttpServletResponse;

public class LoginLambda implements RequestHandler<LoginLambda.Request, LoginLambda.Response> {

    @Override
    public Response handleRequest(Request input, Context context) {
        AWSCognitoDeveloperAuthenticationSample authSample = new AWSCognitoDeveloperAuthenticationSample();
        LambdaLogger logger = context.getLogger();
        logger.log("entering login request");

        try {
            logger.log("Validate parameters");
            String username = input.username;
            String timestamp = input.timestamp;
            String signature = input.signature;
            String uid = input.uid;

            logger.log(String.format("login with username [%s], timestamp [%s], uid [%s]", username, timestamp, uid));

            logger.log("Validate request");
            authSample.validateLoginRequest(username, uid, signature, timestamp);

            logger.log("Get key for user: " + username);
            String data = authSample.getKey(username, uid);
            return validResponse(data);
        } catch (DataAccessException e) {
            logger.log("Failed to access data: " + e.getMessage());
            return errorResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } catch (UnauthorizedException e) {
            logger.log("Unauthorized access due to: " + e.getMessage());
            return errorResponse(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

    private Response errorResponse(int httpResponseCode) {
        return new Response(httpResponseCode, null);
    }

    private Response validResponse(String key) {
        return new Response(HttpServletResponse.SC_OK, key);
    }

    static class Response {

        private int httpResponseCode;
        private String key;

        Response(int httpResponseCode, String key) {
            this.httpResponseCode = httpResponseCode;
            this.key = key;
        }
    }

    static class Request {

        String username;
        String timestamp;
        String signature;
        String uid;

    }
}
