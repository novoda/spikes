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

package com.amazonaws.cognito;

import com.amazonaws.cognito.devauthsample.SampleLogger;
import com.amazonaws.cognito.devauthsample.Configuration;
import com.amazonaws.regions.RegionUtils;
import com.amazonaws.services.cognitoidentity.AmazonCognitoIdentityClient;
import com.amazonaws.services.cognitoidentity.model.GetOpenIdTokenForDeveloperIdentityRequest;
import com.amazonaws.services.cognitoidentity.model.GetOpenIdTokenForDeveloperIdentityResult;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CognitoDeveloperIdentityManagement {
	private static final Logger log = SampleLogger.getLogger();
    
	AmazonCognitoIdentityClient cib;
	
	public CognitoDeveloperIdentityManagement() {
		cib = new AmazonCognitoIdentityClient();
		cib.setRegion(RegionUtils.getRegion(Configuration.REGION));
	}
	
    public GetOpenIdTokenForDeveloperIdentityResult getTokenFromCognito(String username, Map<String,String> logins, String identityId) throws Exception {
    	if ( ( Configuration.IDENTITY_POOL_ID == null ) || username == null ) {
			return null;
		}
		else {
        try {
            GetOpenIdTokenForDeveloperIdentityRequest request = new GetOpenIdTokenForDeveloperIdentityRequest();
            request.setIdentityPoolId(Configuration.IDENTITY_POOL_ID);
            request.setTokenDuration(Long.parseLong(Configuration.SESSION_DURATION));
            request.setLogins(logins);
            if(identityId !=null && !identityId.equals("")){
                request.setIdentityId(identityId);
            }
            log.info("Requesting identity Id: " + identityId);
            GetOpenIdTokenForDeveloperIdentityResult result = cib.getOpenIdTokenForDeveloperIdentity(request);
            log.info("Response identity Id: " + result.getIdentityId());
            return result;
        }
        catch ( Exception exception ) {
            log.log( Level.SEVERE, "Exception during getTemporaryCredentials", exception );
            throw exception;
        }
		}
    }
}
