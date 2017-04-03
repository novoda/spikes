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

package com.amazonaws.cognito.devauthsample.identity;

import com.amazonaws.AmazonClientException;
import com.amazonaws.cognito.devauthsample.Configuration;
import com.amazonaws.cognito.devauthsample.Utilities;
import com.amazonaws.cognito.devauthsample.exception.DataAccessException;
import com.amazonaws.regions.RegionUtils;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * This class is used store and authenticate users. All users and there
 * username/password information is stored in a DynamoDB table.
 */
public class UserAuthentication {

    /**
     * Constant for the table name used to store the identities.
     */
    private static final String USER_TABLE = Configuration.USERS_TABLE;

    /**
     * Constant for the username attribute
     */
    private static final String ATTRIBUTE_USERNAME = "username";

    /**
     * Constant for the hash of password attribute
     */
    private static final String ATTRIBUTE_HASH_SALTED_PASSWORD = "hash_salted_password";

    /**
     * Constant for the enabled attribute
     */
    private static final String ATTRIBUTE_ENABLED = "enabled";

    private final AmazonDynamoDBClient ddb;

    /**
     * Looks up table name and creates one if it does not exist
     */
    public UserAuthentication() {
        ddb = new AmazonDynamoDBClient();
        ddb.setRegion(RegionUtils.getRegion(Configuration.REGION));

        try {
            if (!doesTableExist(USER_TABLE)) {
                createIdentityTable();
            }
        } catch (DataAccessException e) {
            throw new RuntimeException("Failed to create device table.", e);
        }
    }

    /**
     * Returns the list of usernames stored in the identity table.
     *
     * @return list of existing usernames in DynamoDB table
     */
    public List<String> listUsers() {
        List<String> users = new ArrayList<String>(1000);

        ScanResult result = ddb.scan(new ScanRequest().withTableName(USER_TABLE).withLimit(1000));

        for (Map<String, AttributeValue> item : result.getItems()) {
            String s = "";

            for (Entry<String, AttributeValue> entry : item.entrySet()) {
                s += " ** " + entry.getKey() + " = " + entry.getValue().getS();
            }

            users.add(s);
        }

        return users;
    }

    /**
     * Attempts to register the username, password combination. Checks if
     * username not already exist. Returns true if successful, false otherwise.
     *
     * @param username Unique user identifier
     * @param password user password
     * @param uri      endpoint URI
     * @return true if successful, false otherwise.
     * @throws DataAccessException
     */
    public boolean registerUser(String username, String password, String uri) throws DataAccessException {
        if (checkUsernameExists(username)) {
            return false;
        }
        storeUser(username, password, uri);
        return true;
    }

    /**
     * Deletes the specified username from the identity table.
     *
     * @param username Unique user identifier
     * @throws DataAccessException
     */
    public void deleteUser(String username) throws DataAccessException {
        HashMap<String, AttributeValue> key = new HashMap<String, AttributeValue>();
        key.put(ATTRIBUTE_USERNAME, new AttributeValue().withS(username));

        DeleteItemRequest deleteItemRequest = new DeleteItemRequest()
                .withTableName(USER_TABLE)
                .withKey(key);

        try {
            ddb.deleteItem(deleteItemRequest);
        } catch (AmazonClientException e) {
            throw new DataAccessException("Failed to delete user: " + username, e);
        }
    }

    /**
     * Authenticates the given username, password combination. Hash of password
     * is matched against the hash value stored for password field
     *
     * @param username Unique user identifier
     * @param password user password
     * @param uri      endpoint URI
     * @return true if authentication was successful, false otherwise
     * @throws DataAccessException
     */
    public boolean authenticateUser(String username, String password, String uri) throws DataAccessException {
        if (null == username || null == password) {
            return false;
        }

        UserInfo user = getUserInfo(username);
        if (user == null) {
            return false;
        }
        String hashedSaltedPassword = Utilities.getSaltedPassword(username, uri, password);
        return hashedSaltedPassword.equals(user.getHashedPassword());
    }

    /**
     * Authenticates the given username, signature combination. A signature is
     * generated and matched against the given signature. If they match then
     * returns true.
     *
     * @param username  Unique user identifier
     * @param timestamp Timestamp of the request
     * @param signature Signature of the request
     * @return true if authentication was successful, false otherwise
     * @throws DataAccessException
     */
    public boolean authenticateUserSignature(String username, String timestamp, String signature)
            throws DataAccessException {
        UserInfo user = getUserInfo(username);
        if (user == null) {
            return false;
        }

        String computedSignature = Utilities.sign(timestamp, user.getHashedPassword());
        return Utilities.slowStringComparison(signature, computedSignature);
    }

    /**
     * Store the username, password combination in the Identity table. The
     * username will represent the item name and the item will contain a
     * attributes password and userid.
     *
     * @param username Unique user identifier
     * @param password user password
     * @param uri      endpoint URI
     */
    protected void storeUser(String username, String password, String uri) throws DataAccessException {
        if (null == username || null == password) {
            return;
        }

        String hashedSaltedPassword = Utilities.getSaltedPassword(username, uri, password);

        Map<String, AttributeValue> item = new HashMap<String, AttributeValue>();
        item.put(ATTRIBUTE_USERNAME, new AttributeValue().withS(username));
        item.put(ATTRIBUTE_HASH_SALTED_PASSWORD, new AttributeValue().withS(hashedSaltedPassword));
        item.put(ATTRIBUTE_ENABLED, new AttributeValue().withS("true"));

        PutItemRequest putItemRequest = new PutItemRequest()
                .withTableName(USER_TABLE)
                .withItem(item);
        try {
            ddb.putItem(putItemRequest);
        } catch (AmazonClientException e) {
            throw new DataAccessException("Failed to store user: " + username, e);
        }
    }

    /**
     * Used to create the Identity Table. This function only needs to be called
     * once.
     */
    protected void createIdentityTable() throws DataAccessException {
        ProvisionedThroughput provisionedThroughput = new ProvisionedThroughput()
                .withReadCapacityUnits(10L)
                .withWriteCapacityUnits(5L);

        ArrayList<AttributeDefinition> attributeDefinitions = new ArrayList<AttributeDefinition>();
        attributeDefinitions
                .add(new AttributeDefinition().withAttributeName(ATTRIBUTE_USERNAME).withAttributeType("S"));

        ArrayList<KeySchemaElement> tableKeySchema = new ArrayList<KeySchemaElement>();
        tableKeySchema.add(new KeySchemaElement().withAttributeName(ATTRIBUTE_USERNAME).withKeyType(KeyType.HASH));

        CreateTableRequest createTableRequest = new CreateTableRequest()
                .withTableName(USER_TABLE)
                .withProvisionedThroughput(provisionedThroughput)
                .withAttributeDefinitions(attributeDefinitions)
                .withKeySchema(tableKeySchema);

        try {
            ddb.createTable(createTableRequest);
        } catch (AmazonClientException e) {
            throw new DataAccessException("Failed to create table: " + USER_TABLE, e);
        }
    }

    /**
     * Checks to see if given tableName exist
     *
     * @param tableName The table name to check
     * @return true if tableName exist, false otherwise
     */
    protected boolean doesTableExist(String tableName) throws DataAccessException {
        try {
            DescribeTableRequest request = new DescribeTableRequest().withTableName(USER_TABLE);
            DescribeTableResult result = ddb.describeTable(request);
            return "ACTIVE".equals(result.getTable().getTableStatus());
        } catch (ResourceNotFoundException e) {
            return false;
        } catch (AmazonClientException e) {
            throw new DataAccessException("Failed to get status of table: " + tableName, e);
        }
    }

    /**
     * Get user info for the username
     *
     * @param username Unique user identifier
     * @return UserInfo for the user, null otherwise
     */
    public UserInfo getUserInfo(String username) throws DataAccessException {
        HashMap<String, AttributeValue> key = new HashMap<String, AttributeValue>();
        key.put(ATTRIBUTE_USERNAME, new AttributeValue().withS(username));

        GetItemRequest getItemRequest = new GetItemRequest()
                .withTableName(USER_TABLE)
                .withKey(key);

        try {
            return UserInfo.fromData(ddb.getItem(getItemRequest).getItem());
        } catch (AmazonClientException e) {
            throw new DataAccessException("Failed to get item username: " + username, e);
        }
    }

    /**
     * Checks to see if the username already exist in the user table
     *
     * @param username Unique user identifier
     * @return true if username already exist, false otherwise
     * @throws DataAccessException
     */
    public boolean checkUsernameExists(String username) throws DataAccessException {
        return getUserInfo(username) != null;
    }

    /**
     * A class that represents the item stored in user table.
     */
    public static class UserInfo {
        private final String username;
        private final String hashedPassword;
        private final String enabled;

        public UserInfo(String username, String hashedPassword, String enabled) {
            this.username = username;
            this.hashedPassword = hashedPassword;
            this.enabled = enabled;
        }

        public String getUsername() {
            return username;
        }

        public String getHashedPassword() {
            return hashedPassword;
        }

        public String getEnabled() {
            return enabled;
        }

        public static final UserInfo fromData(Map<String, AttributeValue> data) {
            if (data == null || data.isEmpty()) {
                return null;
            }

            return new UserInfo(data.get(ATTRIBUTE_USERNAME).getS(), data.get(ATTRIBUTE_HASH_SALTED_PASSWORD).getS(),
                    data.get(ATTRIBUTE_ENABLED).getS());
        }
    }
}
