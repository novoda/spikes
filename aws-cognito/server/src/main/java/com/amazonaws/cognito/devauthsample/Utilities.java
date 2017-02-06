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

package main.java.com.amazonaws.cognito.devauthsample;

import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;

import com.amazonaws.services.cognitoidentity.model.GetOpenIdTokenForDeveloperIdentityResult;
import com.amazonaws.util.DateUtils;
import com.amazonaws.util.HttpUtils;

public class Utilities {

    protected static final Logger log = AWSCognitoDeveloperAuthenticationSampleLogger.getLogger();
    private static SecureRandom RANDOM = new SecureRandom();
    static {
        RANDOM.generateSeed(16);
    }

    public static String prepareJsonResponseForTokens(GetOpenIdTokenForDeveloperIdentityResult result, String key) {
    	
        StringBuilder responseBody = new StringBuilder();
        responseBody.append("{");
        responseBody.append("\t\"identityPoolId\": \"").append(Configuration.IDENTITY_POOL_ID).append("\",");
        responseBody.append("\t\"identityId\": \"").append(result.getIdentityId()).append("\",");
        responseBody.append("\t\"token\": \"").append(result.getToken()).append("\",");
        responseBody.append("}");

        // Encrypting the response
        return AESEncryption.wrap(responseBody.toString(), key);
    }

    public static String prepareJsonResponseForKey(String data, String key) {

        StringBuilder responseBody = new StringBuilder();
        responseBody.append("{");
        responseBody.append("\t\"key\": \"").append(data).append("\"");
        responseBody.append("}");

        // Encrypting the response
        return AESEncryption.wrap(responseBody.toString(), key.substring(0, 32));
    }

    public static String sign(String content, String key) {
        try {
            byte[] data = content.getBytes(Constants.ENCODING_FORMAT);
            Mac mac = Mac.getInstance(Constants.SIGNATURE_METHOD);
            mac.init(new SecretKeySpec(key.getBytes(Constants.ENCODING_FORMAT), Constants.SIGNATURE_METHOD));
            char[] signature = Hex.encodeHex(mac.doFinal(data));
            return new String(signature);
        } catch (Exception e) {
            log.log(Level.SEVERE, "Exception during sign", e);
        }
        return null;
    }

    public static String getSaltedPassword(String username, String endPointUri, String password) {
        return sign((username + Configuration.APP_NAME + endPointUri.toLowerCase()), password);
    }

    public static String base64(String data) throws UnsupportedEncodingException {
        byte[] signature = Base64.encodeBase64(data.getBytes(Constants.ENCODING_FORMAT));
        return new String(signature, Constants.ENCODING_FORMAT);
    }

    public static String getEndPoint(HttpServletRequest request) {
        if (null == request) {
            return null;
        }
        else {
            String endpoint = request.getServerName().toLowerCase();
            log.info("Endpoint : " + encode(endpoint));
            return endpoint;
        }
    }

    /**
     * Checks to see if the request has valid timestamp. If given timestamp
     * falls in 30 mins window from current server timestamp
     */
    public static boolean isTimestampValid(String timestamp) {
        long timestampLong = 0L;
        final long window = 15 * 60 * 1000L;

        if (null == timestamp) {
            return false;
        }

        timestampLong = DateUtils.parseISO8601Date(timestamp).getTime();
        
        Long now = new Date().getTime();

        long before15Mins = new Date(now - window).getTime();
        long after15Mins = new Date(now + window).getTime();

        return (timestampLong >= before15Mins && timestampLong <= after15Mins);
    }

    public static String generateRandomString() {
        byte[] randomBytes = new byte[16];
        RANDOM.nextBytes(randomBytes);
        String randomString = new String(Hex.encodeHex(randomBytes));
        return randomString;
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
        if (null == str || str.trim().length() == 0)
            return true;
        return false;
    }

    public static String encode(String s) {
        if (null == s)
            return s;
        return HttpUtils.urlEncode(s, false);
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
     * @param json
     *            A string of JSON blob
     * @param element
     *            JSON key
     * @return the corresponding string value of the element
     */
    public static String extractElement(String json, String element) {
        boolean hasElement = (json.indexOf(element) != -1);
        if (hasElement) {
            int elementIndex = json.indexOf(element) + element.length() + 1;
            int startIndex = json.indexOf("\"", elementIndex);
            int endIndex = json.indexOf("\"", startIndex + 1);

            return json.substring(startIndex + 1, endIndex);
        }

        return null;
    }
}
