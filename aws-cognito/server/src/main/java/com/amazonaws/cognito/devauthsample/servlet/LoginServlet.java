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

import com.amazonaws.cognito.devauthsample.Utilities;
import com.amazonaws.cognito.devauthsample.exception.DataAccessException;
import com.amazonaws.cognito.devauthsample.exception.MissingParameterException;
import com.amazonaws.cognito.devauthsample.exception.UnauthorizedException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Level;

/**
 * This class is used to generate encryption key and send
 * back to user. This key is used to encrypt data in further communication. A
 * key is generated if the user supplied credentials are valid
 */
@WebServlet(urlPatterns = "/login/*")
public class LoginServlet extends RootServlet {
    private static final long serialVersionUID = 1L;

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        log.info("entering login request");

        try {
            log.info("Validate parameters");
            String username = getRequiredParameter(request, "username");
            String timestamp = getRequiredParameter(request, "timestamp");
            String signature = getRequiredParameter(request, "signature");
            String uid = getRequiredParameter(request, "uid");

            String endpoint = Utilities.getEndPoint(request);
            log.info(String.format("login with usename [%s], timestamp [%s], uid [%s], endpoint [%s]",
                    username, timestamp, uid, endpoint));

            log.info("Validate request");
            authSample.validateLoginRequest(username, uid, signature, timestamp);

            log.info("Get key for user: " + username);
            String data = authSample.getKey(username, uid);
            sendOKResponse(response, data);
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
        }

        log.info("leaving login request");
    }
}
