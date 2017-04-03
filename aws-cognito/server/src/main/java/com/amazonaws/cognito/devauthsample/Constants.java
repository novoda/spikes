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

package com.amazonaws.cognito.devauthsample;

import org.apache.commons.codec.CharEncoding;
import org.apache.commons.codec.Charsets;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

public class Constants {

    /**
     * Mapping of error code and message
     */
    private static Map<Integer, String> messages = new HashMap<Integer, String>();

    static {
        messages.put(HttpServletResponse.SC_UNAUTHORIZED, "Error matching signature");
        messages.put(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server Error");
        messages.put(HttpServletResponse.SC_OK, "Success");
        messages.put(HttpServletResponse.SC_REQUEST_TIMEOUT, "Timestamp not valid");
    }

    public static String getMsg(int errorCode) {
        String msg = messages.get(errorCode);
        return msg == null ? "Errorcode: " + String.valueOf(errorCode) : msg;
    }

}
