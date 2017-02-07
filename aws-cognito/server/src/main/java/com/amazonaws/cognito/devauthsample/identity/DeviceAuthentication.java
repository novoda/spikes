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
 * This class is used to store and authenticate devices. All devices and their
 * information is stored in a DyanmoDB table.
 */
public class DeviceAuthentication {

    /**
     * Constant for the table name used to store the devices.
     */
    private static final String DEVICE_TABLE = Configuration.DEVICE_TABLE;

    /**
     * Constant for the username attribute
     */
    private static final String ATTRIBUTE_USERNAME = "username";

    /**
     * Constant for the uid (device id) attribute
     */
    private static final String ATTRIBUTE_UID = "uid";

    /**
     * Constant for the key attribute
     */
    private static final String ATTRIBUTE_KEY = "key";

    private final AmazonDynamoDBClient ddb;

    /**
     * Looks up table name and creates one if it does not exist
     */
    public DeviceAuthentication() {
        ddb = new AmazonDynamoDBClient();
        ddb.setRegion(RegionUtils.getRegion(Configuration.REGION));

        try {
            if (!doesTableExist(DEVICE_TABLE)) {
                createDeviceTable();
            }
        } catch (DataAccessException e) {
            throw new RuntimeException("Failed to create device table.", e);
        }
    }

    /**
     * @return the list of device ID (UID) stored in the identity table.
     */
    public List<String> listDevices() {
        List<String> devices = new ArrayList<String>(1000);

        ScanResult result = ddb.scan(new ScanRequest().withTableName(DEVICE_TABLE).withLimit(1000));

        for (Map<String, AttributeValue> item : result.getItems()) {
            String s = "";

            for (Entry<String, AttributeValue> entry : item.entrySet()) {
                s += " ** " + entry.getKey() + " = " + entry.getValue().getS();
            }

            devices.add(s);
        }

        return devices;
    }

    /**
     * Returns device info for given device ID (UID)
     *
     * @param uid Unique device identifier
     * @return device info for the given uid
     */
    public DeviceInfo getDeviceInfo(String uid) throws DataAccessException {
        HashMap<String, AttributeValue> key = new HashMap<String, AttributeValue>();
        key.put(ATTRIBUTE_UID, new AttributeValue().withS(uid));

        GetItemRequest getItemRequest = new GetItemRequest()
                .withTableName(DEVICE_TABLE)
                .withKey(key);

        try {
            return DeviceInfo.fromData(ddb.getItem(getItemRequest).getItem());
        } catch (AmazonClientException e) {
            throw new DataAccessException("Failed to get device: " + uid, e);
        }
    }

    /**
     * Attempts to register the UID, Key and username combination. Returns true
     * if successful, false otherwise.
     *
     * @param uid      Unique device identifier
     * @param key      encryption key associated with UID
     * @param username Unique user identifier
     * @return true if device registration was successful, false otherwise
     * @throws DataAccessException
     */
    public boolean registerDevice(String uid, String key, String username) throws DataAccessException {
        DeviceInfo device = getDeviceInfo(uid);
        if (device != null && !username.equals(device.getUsername())) {
            return false;
        }
        storeDevice(uid, key, username);
        return true;
    }

    /**
     * Deletes the specified UID from the identity table.
     *
     * @param uid Unique device identifier
     */
    public void deleteDevice(String uid) throws DataAccessException {
        HashMap<String, AttributeValue> key = new HashMap<String, AttributeValue>();
        key.put(ATTRIBUTE_UID, new AttributeValue().withS(uid));

        DeleteItemRequest deleteItemRequest = new DeleteItemRequest()
                .withTableName(DEVICE_TABLE)
                .withKey(key);

        try {
            ddb.deleteItem(deleteItemRequest);
        } catch (AmazonClientException e) {
            throw new DataAccessException("Failed to delete device: " + uid, e);
        }
    }

    /**
     * Authenticates the given UID, Key combination. If the password in the item
     * identified by the item name 'UID' matches the Key given then true is
     * returned, false otherwise.
     *
     * @param uid Unique device identifier
     * @param key encryption key associated with UID
     * @return true if authentication was successful, false otherwise
     * @throws DataAccessException
     */
    public boolean authenticateDevice(String uid, String key) throws DataAccessException {
        DeviceInfo device = getDeviceInfo(uid);
        return device != null && key.equals(device.getKey());
    }

    /**
     * Store the UID, Key, username combination in the Identity table. The UID
     * will represent the item name and the item will contain attributes key and
     * username.
     *
     * @param uid      Unique device identifier
     * @param key      encryption key associated with UID
     * @param username Unique user identifier
     */
    protected void storeDevice(String uid, String key, String username) throws DataAccessException {
        Map<String, AttributeValue> item = new HashMap<String, AttributeValue>();
        item.put(ATTRIBUTE_UID, new AttributeValue().withS(uid));
        item.put(ATTRIBUTE_KEY, new AttributeValue().withS(key));
        item.put(ATTRIBUTE_USERNAME, new AttributeValue().withS(username));

        PutItemRequest putItemRequest = new PutItemRequest()
                .withTableName(DEVICE_TABLE)
                .withItem(item);
        try {
            ddb.putItem(putItemRequest);
        } catch (AmazonClientException e) {
            throw new DataAccessException(String.format("Failed to store device uid: %s; key: %s; username: %s", uid,
                    key, username), e);
        }
    }

    /**
     * Used to create the device table. This function only needs to be called
     * once.
     */
    protected void createDeviceTable() throws DataAccessException {
        ProvisionedThroughput provisionedThroughput = new ProvisionedThroughput()
                .withReadCapacityUnits(10L)
                .withWriteCapacityUnits(5L);

        ArrayList<AttributeDefinition> attributeDefinitions = new ArrayList<AttributeDefinition>();
        attributeDefinitions.add(new AttributeDefinition().withAttributeName(
                ATTRIBUTE_UID).withAttributeType("S"));

        ArrayList<KeySchemaElement> tableKeySchema = new ArrayList<KeySchemaElement>();
        tableKeySchema.add(new KeySchemaElement().withAttributeName(ATTRIBUTE_UID)
                .withKeyType(KeyType.HASH));

        CreateTableRequest createTableRequest = new CreateTableRequest()
                .withTableName(DEVICE_TABLE)
                .withProvisionedThroughput(provisionedThroughput)
                .withAttributeDefinitions(attributeDefinitions)
                .withKeySchema(tableKeySchema);

        try {
            ddb.createTable(createTableRequest);
        } catch (AmazonClientException e) {
            throw new DataAccessException("Failed to create table: " + DEVICE_TABLE, e);
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
            DescribeTableRequest request = new DescribeTableRequest().withTableName(tableName);
            DescribeTableResult result = ddb.describeTable(request);
            return "ACTIVE".equals(result.getTable().getTableStatus());
        } catch (ResourceNotFoundException e) {
            return false;
        } catch (AmazonClientException e) {
            throw new DataAccessException("Failed to get status of table: " + tableName, e);
        }
    }

    /**
     * Checks to see if the device id (UID) already exist in the device table
     *
     * @param uid Unique device identifier
     * @return true if the given UID already exist, false otherwise
     * @throws DataAccessException
     */
    public boolean checkUidExists(String uid) throws DataAccessException {
        return getDeviceInfo(uid) != null;
    }

    public static class DeviceInfo {
        private final String uid;
        private final String key;
        private final String username;

        public DeviceInfo(String uid, String key, String username) {
            this.uid = uid;
            this.key = key;
            this.username = username;
        }

        public String getUid() {
            return uid;
        }

        public String getKey() {
            return key;
        }

        public String getUsername() {
            return username;
        }

        private static DeviceInfo fromData(Map<String, AttributeValue> data) {
            if (data == null || data.isEmpty()) {
                return null;
            }

            return new DeviceInfo(data.get(ATTRIBUTE_UID).getS(), data.get(ATTRIBUTE_KEY).getS(),
                    data.get(ATTRIBUTE_USERNAME).getS());
        }
    }
}
