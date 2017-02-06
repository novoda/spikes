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

/**
 * This interface exposes key functions performed by AWSCognitoDeveloperAuthenticationSample.
 * Application developer can track their customer and how much
 * resources each of them is using by registering the users and then
 * generating tokens based on a particular user.
 */
public interface AWSCognitoDeveloperAuthenticationSample {

    /**
     * Allows users to register with AWSCognitoDeveloperAuthenticationSample.
     * 
     * @param username
     *            Unique alphanumeric string of length between 3 to 128
     *            characters with special characters limited to underscore (_)
     *            and period (.)
     * @param password
     *            String of length between 6 to 128 characters
     * @param endpoint
     *            DNS name of host machine
     * @return status code indicating if the registration was successful or not
     * @throws Exception
     */
    int registerUser(String username, String password, String endpoint) throws Exception;

    /**
     * Verify if the token request is valid. UID is authenticated. The timestamp
     * is checked to see it falls within the valid timestamp window. The
     * signature is computed and matched against the given signature.
     * 
     * @param uid
     *            Unique device identifier
     * @param signature
     *            Base64 encoded HMAC-SHA256 signature derived from key and
     *            timestamp
     * @param timestamp
     *            Timestamp of the request in ISO8601 format
     * @return status code indicating if token request is valid or not
     * @throws Exception
     */
    int validateTokenRequest(String uid, String signature, String timestamp) throws Exception;

    /**
     * Generate tokens for given UID. The tokens are encrypted using the key
     * corresponding to UID. Encrypted tokens are then wrapped in JSON object
     * before returning it.
     * 
     * @param uid
     *            Unique device identifier
     * @return encrypted tokens as JSON object
     * @throws Exception
     */
    String getToken(String uid) throws Exception;

    /**
     * Verify if the login request is valid. Username and UID are authenticated.
     * The timestamp is checked to see it falls within the valid timestamp
     * window. The signature is computed and matched against the given
     * signature. Also its checked to see if the UID belongs to the username.
     * 
     * @param username
     *            Unique user identifier
     * @param uid
     *            Unique device identifier
     * @param signature
     *            Base64 encoded HMAC-SHA256 signature derived from hash of
     *            salted-password and timestamp
     * @param timestamp
     *            Timestamp of the request in ISO8601 format
     * @return status code indicating if login request is valid or not
     * @throws Exception
     */
    int validateLoginRequest(String username, String uid, String signature, String timestamp) throws Exception;

    /**
     * Generate key for device UID. The key is encrypted by hash of salted
     * password of the user. Encrypted key is then wrapped in JSON object before
     * returning it.
     * 
     * @param username
     *            Unique user identifier
     * @param uid
     *            Unique device identifier
     * @return encrypted key as JSON object
     * @throws Exception
     */
    String getKey(String username, String uid) throws Exception;

}