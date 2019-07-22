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

package com.amazonaws.cognito.devauthsample.servlet;

import com.amazonaws.cognito.devauthsample.exception.DataAccessException;
import com.amazonaws.cognito.devauthsample.exception.MissingParameterException;
import com.amazonaws.cognito.devauthsample.exception.UnauthorizedException;
import com.amazonaws.cognito.devauthsample.identity.DeviceAuthentication;
import com.amazonaws.cognito.devauthsample.identity.UserAuthentication;
import com.google.firebase.auth.FirebaseAuth;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

/**
 * Servlet implementation class GetFirebaseTokenServlet
 */
@WebServlet(urlPatterns = "/getfirebasetoken/*")
public class GetFirebaseTokenServlet extends RootServlet {
    private static final long serialVersionUID = 1L;

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        log.info("entering get token request");

        try {
            log.info("Validate parameters");
            String uid = getRequiredParameter(request, "uid");
            String signature = getRequiredParameter(request, "signature");
            String timestamp = getRequiredParameter(request, "timestamp");
            String identityId = getParameter(request, "identityId");
            HashMap<String, String> logins = new HashMap<String, String>();

            // build the string to sign
            StringBuilder stringToSign = new StringBuilder();
            stringToSign.append(timestamp);
            // process any login tokens passed in
            boolean foundLogin = true;
            int loginNum = 1;
            while (foundLogin) {
                String provider = request.getParameter("provider" + loginNum);
                String token = request.getParameter("token" + loginNum);

                foundLogin = (provider != null) && (token != null);
                if (foundLogin) {
                    log.info(String.format("adding token from [%s]", provider));
                    logins.put(provider, token);
                    stringToSign.append(provider);
                    stringToSign.append(token);
                    loginNum++;
                }
            }

            if (identityId != null) {
                stringToSign.append(identityId);
            }
            log.info(String.format("Get token with uid [%s] timestamp [%s]", uid, timestamp));

            log.info("validate token request");
            authSample.validateTokenRequest(uid, signature, timestamp, stringToSign.toString());

            log.info("get token for device: " + uid);

            final String token = authSample.getFirebaseToken(uid);
            sendOKResponse(response, token);
        } catch (MissingParameterException e) {
            log.warning("Missing parameter: " + e.getMessage() + ". Setting Http status code "
                    + HttpServletResponse.SC_BAD_REQUEST);
            sendErrorResponse(HttpServletResponse.SC_BAD_REQUEST, response);
        } catch (DataAccessException e) {
            log.log(Level.SEVERE, "Failed to access data", e);
            sendErrorResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, response);
        } catch (UnauthorizedException e) {
            log.warning("Unauthorized access due to: " + e.getMessage());
            sendErrorResponse(HttpServletResponse.SC_UNAUTHORIZED, response);
        } catch (Exception e) {
            log.warning("Exception due to: " + e.getMessage());
            sendErrorResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, response);
        }

        log.info("leaving get token request");
    }

}
