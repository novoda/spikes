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

import java.util.logging.Logger;

/**
 * This class captures all of the configuration settings. These environment
 * properties are defined in the BeanStalk container configuration tab.
 */
public class Configuration {

    protected static final Logger log = AWSCognitoDeveloperAuthenticationSampleLogger
            .getLogger();

    /**
     * The identity pool to use
     */
    public static final String IDENTITY_POOL_ID = ((System
            .getProperty("IDENTITY_POOL_ID") == null) || System.getProperty(
            "IDENTITY_POOL_ID").isEmpty()) ? System.getProperty("PARAM1")
            : System.getProperty("IDENTITY_POOL_ID");

    /**
     * The developer provider name to use
     */
    public static final String DEVELOPER_PROVIDER_NAME = ((System
            .getProperty("DEVELOPER_PROVIDER_NAME") == null) || System
            .getProperty("DEVELOPER_PROVIDER_NAME").isEmpty()) ? System
            .getProperty("PARAM2") : System
            .getProperty("DEVELOPER_PROVIDER_NAME");

    /**
     * The application name
     */
    public static final String APP_NAME = getAppName();

    /**
     * The region to run against
     */
    public static final String REGION = getRegion();

    /**
     * The duration for which the open id token will be valid
     */
    public static final String SESSION_DURATION = "900";

    /**
     * The name of the DynamoDB Table used to store user info if using the
     * custome authentication mechanisms.
     */
    public static final String USERS_TABLE = getUsersTable();

    /**
     * The name of the DynamoDB Table used to store device info if using the
     * custome authentication mechanisms.
     */
    public static final String DEVICE_TABLE = getDeviceTable();

    private static String getAppName() {
        return System.getProperty("APP_NAME",
                "AWSCognitoDeveloperAuthenticationSample").toLowerCase();
    }

    private static String getUsersTable() {
        return "AWSCognitoDeveloperAuthenticationSample_" + APP_NAME + "_USERS";
    }

    private static String getDeviceTable() {
        return "AWSCognitoDeveloperAuthenticationSample_" + APP_NAME
                + "_DEVICES";
    }

    private static String getRegion() {
        return System.getProperty("REGION", "us-east-1");
    }

}
