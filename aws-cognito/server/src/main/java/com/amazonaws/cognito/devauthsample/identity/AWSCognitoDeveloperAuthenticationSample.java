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

package main.java.com.amazonaws.cognito.devauthsample.identity;

import java.util.logging.Logger;
import java.util.Map;

import main.java.com.amazonaws.cognito.CognitoDeveloperIdentityManagement;
import main.java.com.amazonaws.cognito.devauthsample.AWSCognitoDeveloperAuthenticationSampleLogger;
import main.java.com.amazonaws.cognito.devauthsample.Configuration;
import main.java.com.amazonaws.cognito.devauthsample.Utilities;
import main.java.com.amazonaws.cognito.devauthsample.exception.DataAccessException;
import main.java.com.amazonaws.cognito.devauthsample.exception.UnauthorizedException;
import main.java.com.amazonaws.cognito.devauthsample.identity.DeviceAuthentication.DeviceInfo;
import main.java.com.amazonaws.cognito.devauthsample.identity.UserAuthentication.UserInfo;
import com.amazonaws.services.cognitoidentity.model.GetOpenIdTokenForDeveloperIdentityResult;

/**
 * This class allows new users to register by providing username and password combination.
 * Registered users can then obtain encryption key after login.
 * This key is used to encrypt tokens in future communication.
 */

public class AWSCognitoDeveloperAuthenticationSample {

    private static final Logger log = AWSCognitoDeveloperAuthenticationSampleLogger.getLogger();

    private final DeviceAuthentication deviceAuthenticator;
    private final UserAuthentication userAuthenticator;
    private final CognitoDeveloperIdentityManagement byoiManagement;

    public AWSCognitoDeveloperAuthenticationSample() {
        deviceAuthenticator = new DeviceAuthentication();
        userAuthenticator = new UserAuthentication();
        byoiManagement = new CognitoDeveloperIdentityManagement();
    }

    /**
     * Verify if the given signature is valid.
     * 
     * @param stringToSign
     *            The string to sign
     * @param key
     *            The key used in the signature process
     * @param signature
     *            Base64 encoded HMAC-SHA256 signature derived from key and
     *            string
     * @return true if computed signature matches with the given signature,
     *         false otherwise
     */
    public boolean validateSignature(String stringToSign, String key, String targetSignature) {
        String computedSignature = Utilities.sign(stringToSign, key);
        return Utilities.slowStringComparison(targetSignature, computedSignature);
    }

    /**
     * Allows users to register with AWSCognitoDeveloperAuthenticationSample. This function
     * is useful in Identity mode
     * 
     * @param username
     *            Unique alphanumeric string of length between 3 to 128
     *            characters with special characters limited to underscore (_),
     *            period (.) and (@).
     * @param password
     *            String of length between 6 to 128 characters
     * @param endpoint
     *            DNS name of host machine
     * @return boolean indicating if the registration was successful or not
     * @throws DataAccessException
     */
    public boolean registerUser(String username, String password, String endpoint) throws DataAccessException {
        return userAuthenticator.registerUser(username, password, endpoint);
    }

    /**
     * Verify if the token request is valid. UID is authenticated. The timestamp
     * is checked to see it falls within the valid timestamp window. The
     * signature is computed and matched against the given signature. Useful in
     * Anonymous and Identity modes
     * 
     * @param uid
     *            Unique device identifier
     * @param signature
     *            Base64 encoded HMAC-SHA256 signature derived from key and
     *            timestamp
     * @param timestamp
     *            Timestamp of the request in ISO8601 format
     * @throws DataAccessException
     * @throws UnauthorizedException
     */
    public void validateTokenRequest(String uid, String signature, String timestamp, 
            String stringToSign) throws DataAccessException, UnauthorizedException {
        if (!Utilities.isTimestampValid(timestamp)) {
            throw new UnauthorizedException("Invalid timestamp: " + timestamp);
        }
        log.info(String.format("Timestamp [ %s ] is valid", timestamp));

        DeviceInfo device = deviceAuthenticator.getDeviceInfo(uid);
        if (device == null) {
            throw new UnauthorizedException("Couldn't find device: " + uid);
        }

        if (!validateSignature(stringToSign, device.getKey(), signature)) {
            log.info("String to sign: " + stringToSign);
            throw new UnauthorizedException("Invalid signature: " + signature);
        }
        log.info("Signature matched!!!");
    }

    /**
     * Generate tokens for given UID. The tokens are encrypted using the key
     * corresponding to UID. Encrypted tokens are then wrapped in JSON object
     * before returning it. Useful in Anonymous and Identity modes
     * 
     * @param uid
     *            Unique device identifier
     * @return encrypted tokens as JSON object
     * @throws Exception 
     */
    public String getToken(String uid, Map<String,String> logins, String identityId) 
        throws Exception {

        DeviceInfo device = deviceAuthenticator.getDeviceInfo(uid);
        if (device == null) {
            throw new UnauthorizedException("Couldn't find device: " + uid);
        }

        UserInfo user = userAuthenticator.getUserInfo(device.getUsername());
        if (user == null) {
            throw new UnauthorizedException("Couldn't find user: " + device.getUsername());
        }
        
        if (user != null && !user.getUsername().equals(logins.get(Configuration.DEVELOPER_PROVIDER_NAME))) {
            throw new UnauthorizedException("User mismatch for device and logins map");
        }

        log.info("Creating temporary credentials");
        GetOpenIdTokenForDeveloperIdentityResult result = byoiManagement.getOpenIdTokenFromCognito(user.getUsername(), logins, identityId);

        log.info("Generating session tokens for UID : " + uid);
        return Utilities.prepareJsonResponseForTokens(result, device.getKey());
    }

    /**
     * Verify if the login request is valid. Username and UID are authenticated.
     * The timestamp is checked to see it falls within the valid timestamp
     * window. The signature is computed and matched against the given
     * signature. Also its checked to see if the UID belongs to the username.
     * This function is useful in Identity mode
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
     * @throws DataAccessException
     * @throws UnauthorizedException
     */
    public void validateLoginRequest(String username, String uid, String signature, String timestamp)
            throws DataAccessException, UnauthorizedException {
        if (!Utilities.isTimestampValid(timestamp)) {
            throw new UnauthorizedException("Invalid timestamp: " + timestamp);
        }

        log.info(String.format("Timestamp [ %s ] is valid", timestamp));

        // Validate signature
        log.info("Validate signature: " + signature);
        UserInfo user = userAuthenticator.getUserInfo(username);
        if (user == null) {
            throw new UnauthorizedException("Couldn't find user: " + username);
        }

        if (!validateSignature(timestamp, user.getHashedPassword(), signature)) {
            throw new UnauthorizedException("Invalid signature: " + signature);
        }

        log.info("Signature matched!!!");

        // Register device
        DeviceInfo device = regenerateKey(uid, user.getUsername());
        log.info("Device found/registered successfully!!!");

        if (!deviceBelongsToUser(user.getUsername(), device.getUsername())) {
            throw new UnauthorizedException(String.format("User [ %s ] doesn't match the device's owner [ %s ]",
                    user.getUsername(), device.getUsername()));
        }
    }

    /**
     * Generate key for device UID. The key is encrypted by hash of salted
     * password of the user. Encrypted key is then wrapped in JSON object before
     * returning it. This function is useful in Identity mode
     * 
     * @param username
     *            Unique user identifier
     * @param uid
     *            Unique device identifier
     * @return encrypted key as JSON object
     * @throws DataAccessException
     * @throws UnauthorizedException
     */
    public String getKey(String username, String uid) throws DataAccessException, UnauthorizedException {
        DeviceInfo device = deviceAuthenticator.getDeviceInfo(uid);
        if (device == null) {
            throw new UnauthorizedException("Couldn't find device: " + uid);
        }

        UserInfo user = userAuthenticator.getUserInfo(username);
        if (user == null) {
            throw new UnauthorizedException("Couldn't find user: " + username);
        }

        log.info("Responding with encrypted key for UID : " + uid);
        return Utilities.prepareJsonResponseForKey(device.getKey(), user.getHashedPassword());
    }

    /**
     * This method regenerates the key each time. It lookups up device details
     * of a registered device. Also registers device if it is not already
     * registered.
     * 
     * @param uid
     *            Unique device identifier
     * @param username
     *            Userid of the current user
     * @return device info i.e. key and userid
     * @throws DataAccessException
     */
    private DeviceInfo regenerateKey(String uid, String username)
            throws DataAccessException {
        log.info("Generating encryption key");
        String encryptionKey = Utilities.generateRandomString();

        if (deviceAuthenticator.registerDevice(uid, encryptionKey, username)) {
            return deviceAuthenticator.getDeviceInfo(uid);
        }

        return null;
    }

    /**
     * Checks of the device UID belongs to the given user
     * 
     * @param useridFromUser
     *            Userid of the current user
     * @param useridFromDevice
     *            Userid associated with the given UID
     * @return true if device UID belongs to current user, false otherwise
     */
    private boolean deviceBelongsToUser(String useridFromUser, String useridFromDevice) {
        return useridFromUser.equals(useridFromDevice);
    }
}
