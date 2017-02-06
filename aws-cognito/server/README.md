# AWSCognitoSampleDeveloperAuthenticationSample

**Overview**

This sample application demonstrates the developer-authenticated functionality of Amazon Cognito. Use this sample in conjunction with the CognitoSyncDemo sample for [iOS](https://github.com/awslabs/aws-sdk-ios-samples/tree/master/CognitoSync-Sample/Objective-C) or [Android](https://github.com/awslabs/aws-sdk-android-samples/tree/master/CognitoSyncDemo).

**Prerequisites for use**

- [Create an AWS Account](http://aws.amazon.com/)
- [Install the AWS Mobile SDK](http://aws.amazon.com/mobile/sdk/?nc2=h_l3_ms)
- Download one of the CognitoSyncDemo samples for [iOS](https://github.com/awslabs/aws-sdk-ios-samples/tree/master/CognitoSync-Sample/Objective-C) or [Android](https://github.com/awslabs/aws-sdk-android-samples/tree/master/CognitoSyncDemo)
- Read about [developer-authenticated identities](http://docs.aws.amazon.com/cognito/devguide/identity/developer-authenticated-identities/) in the [Amazon Cognito Developer Guide](http://docs.aws.amazon.com/cognito/devguide/)

**Additional recommendations**

None.

**Overview of sample setup process**

1. Set up an identity pool that supports developer-authenticated identities.
2. Do one of the following:
	- **Option 1**: Do a Quick Start Deployment using the sample using Amazon CloudFormation.
  	- **Option 2**: Build the sample yourself and deploy using Amazon Elastic Beanstalk.

**Additional resources**

- **Read** the [Amazon Cognito Developer Guide](http://docs.aws.amazon.com/cognito/devguide/)
- **Read** the [Identity API Reference](http://docs.aws.amazon.com/cognitoidentity/latest/APIReference/Welcome.html)
- **Ask us questions** on the [Amazon Cognito Forums](https://forums.aws.amazon.com/forum.jspa?forumID=173) or open an issue on Github

# Set up an identity pool

**I already have an identity pool set up that supports developer-authenticated identities.**

Do one of the following:

- **Option 1**: Do a Quick Start Deployment using the sample using Amazon CloudFormation.	
- **Option 2**: Build the sample yourself and deploy using Amazon Elastic Beanstalk.

**I have an identity pool set up but I am unsure if it supports developer-authenticated identities.**

1. Go to the [Amazon Cognito console](https://console.aws.amazon.com/cognito), and then click the identity pool that you want to use.
2. Click **Edit Identity Pool**.
3. On the next screen, expand **Authentication providers**, and then click the **Custom** tab. Enter a DeveloperProviderName that you want to use for your application (e.g. login.myapp), if one does not exist.
4. Click **Save Changes**.
5. Use the identity pool id provided by the sample code in your application. Instructions on how to use the identity pool id in your desired sample app can be found in the ReadMe instructions for that sample.

**I do not have an identity pool.**

1. Go to the [Amazon Cognito console](https://console.aws.amazon.com/cognito), and then click **New Identity Pool**.
2. On the next screen:
	a. Provide a valid identity pool name.
	b. In the **Developer Authenticated Identities** section, provide a DeveloperProviderName that you want to use for your application (e.g. login.myapp).
	c. Click **Create Pool**.
4. On the next page, click **Update Roles**. This allows Cognito to create roles on your behalf for the identity pool.
4. Use the identity pool id provided by the sample code in your application. Instructions on how to use the identity pool id in your desired sample app can be found in the ReadMe instructions for that sample.

When finished setting up an identity pool, proceed to Option 1 or Option 2 below.


# Option 1: Quick start deployment using Amazon CloudFormation

1. Go to the [Cloud Formation console](https://console.aws.amazon.com/cloudformation/), and then click **Create Stack**.
2. Provide a stack name (e.g. myTestStack).
3. Select "Specify an Amazon S3 template URL" and enter either of the following URL as a template based on the choice of the region, and then click **Next**.

		US EAST (N. Virginia) : https://s3.amazonaws.com/amazon-cognito-samples-us-east-1/AWSCognitoDeveloperAuthenticationSampleCFN.json
		
		EU (Ireland) : https://s3.amazonaws.com/amazon-cognito-samples-eu-west-1/AWSCognitoDeveloperAuthenticationSampleCFN.json
		
		Asia Pacific (Tokyo) : https://s3.amazonaws.com/amazon-cognito-samples-ap-northeast-1/AWSCognitoDeveloperAuthenticationSampleCFN.json
		
4. Enter the DeveloperProviderName and IdentityPoolId associated with the identity pool you want to use, and then click **Next**.
5. On the **Options** page, click **Next**.
6. On the **Review** page, review the details and select the checkbox acknowledging that your template has capabilities to create AWS IAM resources. When finished, click **Create**. It may take several minutes for the stack to finish creating resources. 

	Once the stack is created, the console will display the ApplicationURL in the output tab. You will use this URL in your Android or iOS sample. This allows your app to communicate with the server application. You can now register users using any web browser using the following URL: 

		YourApplicationURL/jsp/register.jsp


# Option 2: Build and deploy the server application yourself using Amazon Elastic Beanstalk

1. Build the AWSCognitoSampleDeveloperAuthenticationSample to get the war file by running `mvn clean install` in the root directory. You will need [Apache Maven](http://maven.apache.org/download.cgi) installed on your system to run this command. The .war file is generated in the /target directory.
2. Go to the [Elastic Beanstalk console](https://console.aws.amazon.com/elasticbeanstalk), and then click **Create New Application**.
3. Enter an application name, and then click **Next**.
4. Click **Create web server**. The Permissions window appears.
5. Choose the default profile ("aws-elasticbeanstalk-ec2-role"), and then click **Next**.
6. For Predefined Configuration, choose Tomcat. For Environment type, choose Single instance. Click **Next**.
7. For the source, choose the **Upload your own** option and upload the CognitoDevAuthSample.war file you generated in Step 1. Click **Next**.
8. On the **Environment Information** page, enter an environment name, URL, and description, and then click **Next**.
9. On the **Additional Resources** page, no additional resources are required to configure this sample. Click **Next**.
10. On the **Configuration Details** page, leave the default configuration, and then click **Next**.
11. On the **Environment Tags** page, leave the tags blank. This sample does not require any environment tags. Click **Next**.
12. On the **Review** page, review the details, and then click **Launch**. The console will begin creating an environment. This process will take several minutes.
13. Click **Configuration** , and then click the settings icon to open Software Configuration.
14. Under **Environment properties** , add the following parameters in the custom input field at the bottom of the Property Name column:
	a. Add a parameter named IDENTITY\_POOL\_ID, with the value of identity pool id which you have configured using the Amazon Cognito Console.
	b. Add a parameter named DEVELOPER\_PROVIDER\_NAME, with the developer provider name which you used while creating this identity pool in the Amazon Cognito Console
	c. Optionally add a parameter named REGION with the region to use. Cognito is available in `us-east-1` and `eu-west-1`.
	d. When finished, click **Save**.
15. Go to the [Identity & Access Management](https://console.aws.amazon.com/iam/) console.
16. Click **Roles**.
17. Select the entry for "aws-elasticbeanstalk-ec2-role."
18. Click **Attach" Policy**.
19. Choose "AmazonCognitoDeveloperAuthenticatedIdentities," and then click **Attach Policy**.
20. Register users using any web browser using ElasticBeanStalkApplicationURL/jsp/register.jsp. This URL is listed at the top of the Elastic Beanstalk console homepage and ends in ".elasticbeanstalk.com."

You are now set up to run the sample server application. You can configure the CognitoSyncDemo sample application for [iOS](https://github.com/awslabs/aws-sdk-ios-samples/tree/master/CognitoSync-Sample/Objective-C) or [Android](https://github.com/awslabs/aws-sdk-android-samples/tree/master/CognitoSyncDemo) to authenticate registered users using this application.
