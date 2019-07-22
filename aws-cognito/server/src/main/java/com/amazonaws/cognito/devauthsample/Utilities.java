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

import com.amazonaws.services.cognitoidentity.model.GetOpenIdTokenForDeveloperIdentityResult;
import org.apache.commons.codec.CharEncoding;
import org.apache.commons.codec.binary.Hex;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Utilities {

    private static final String ENCODING_FORMAT = CharEncoding.UTF_8;
    private static final String SIGNATURE_METHOD = "HmacSHA256";
    private static final Logger log = SampleLogger.getLogger();

    public static String prepareCognitoJsonResponseForTokens(GetOpenIdTokenForDeveloperIdentityResult result, String key) {
        String body = "{" +
                "\t\"identityPoolId\": \"" + Configuration.IDENTITY_POOL_ID + "\"," +
                "\t\"identityId\": \"" + result.getIdentityId() + "\"," +
                "\t\"token\": \"" + result.getToken() + "\"," +
                "}";

        // Encrypting the response
        return AESEncryption.wrap(body, key);
    }

    public static String prepareFirebaseJsonResponseForToken(String token, String key) {
        String body = "{" +
                "\t\"token\": \"" + token + "\"" +
                "}";

        // Encrypting the response
        return AESEncryption.wrap(body, key);
    }

    public static String prepareJsonResponseForKey(String data, String key) {

        String body = "{" +
                "\t\"key\": \"" + data + "\"" +
                "}";

        // Encrypting the response
        return AESEncryption.wrap(body, key.substring(0, 32));
    }

    public static String sign(String content, String key) {
        try {
            byte[] data = content.getBytes(ENCODING_FORMAT);
            Mac mac = Mac.getInstance(SIGNATURE_METHOD);
            mac.init(new SecretKeySpec(key.getBytes(ENCODING_FORMAT), SIGNATURE_METHOD));
            char[] signature = Hex.encodeHex(mac.doFinal(data));
            return new String(signature);
        } catch (Exception e) {
            log.log(Level.SEVERE, "Exception during sign", e);
        }
        return null;
    }

    public static String getSaltedPassword(String username, String endPointUri, String password) {
        return sign(username + Configuration.APP_NAME + endPointUri.toLowerCase(), password);
    }

    public static String getEndPoint(HttpServletRequest request) {
        if (null == request) {
            return null;
        } else {
            String endpoint = request.getServerName().toLowerCase();
            log.info("Endpoint : " + encode(endpoint));
            return endpoint;
        }
    }

    public static boolean isValidUsername(String username) {
        int length = username.length();
        if (length < 3 || length > 128) {
            return false;
        }

        char c = 0;
        for (int i = 0; i < length; i++) {
            c = username.charAt(i);
            if (!Character.isLetterOrDigit(c) && '_' != c && '.' != c && '@' != c) {
                return false;
            }
        }

        return true;
    }

    public static boolean isValidPassword(String password) {
        int length = password.length();
        return (length >= 6 && length <= 128);
    }

    public static boolean isEmpty(String str) {
        return null == str || str.trim().length() == 0;
    }

    private static String encode(String s) {
        if (null == s)
            return s;
        try {
            return URLEncoder.encode(s, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * This method is low performance string comparison function. The purpose of
     * this method is to prevent timing attack.
     */
    public static boolean slowStringComparison(String givenSignature, String computedSignature) {
        if (null == givenSignature || null == computedSignature
                || givenSignature.length() != computedSignature.length())
            return false;

        int n = computedSignature.length();
        boolean signaturesMatch = true;

        for (int i = 0; i < n; i++) {
            signaturesMatch &= (computedSignature.charAt(i) == givenSignature.charAt(i));
        }

        return signaturesMatch;
    }

    /**
     * Extract element from a JSON string
     *
     * @param json    A string of JSON blob
     * @param element JSON key
     * @return the corresponding string value of the element
     */
    public static String extractElement(String json, String element) {
        if ((json.contains(element))) {
            int elementIndex = json.indexOf(element) + element.length() + 1;
            int startIndex = json.indexOf("\"", elementIndex);
            int endIndex = json.indexOf("\"", startIndex + 1);

            return json.substring(startIndex + 1, endIndex);
        }

        return null;
    }
}
