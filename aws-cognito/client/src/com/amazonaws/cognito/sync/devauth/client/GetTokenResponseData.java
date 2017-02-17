/**
 * Copyright 2010-2014 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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

package com.amazonaws.cognito.sync.devauth.client;

/**
 * This class is used to store the response of the GetToken call of the sample
 * Cognito developer authentication.
 */
public class GetTokenResponseData extends ResponseData {
    private final String identityId;
    private final String identityPoolId;
    private final String token;

    public GetTokenResponseData(final int responseCode, final String responseMessage) {
        super(responseCode, responseMessage);
        this.identityId = null;
        this.identityPoolId = null;
        this.token = null;
    }

    public GetTokenResponseData(final String identityId,
                                final String identityPoolId, final String token) {
        super(200, null);
        this.identityId = identityId;
        this.identityPoolId = identityPoolId;
        this.token = token;
    }

    public String getIdentityId() {
        return this.identityId;
    }

    public String getIdentityPoolId() {
        return this.identityPoolId;
    }

    public String getToken() {
        return this.token;
    }
}
