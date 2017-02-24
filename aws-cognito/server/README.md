This is an adjusted sample to showcase a server using Cognito Developer Authentication.

#### Changes from the original

- Use Gradle to build
- Adjust package structure (remove `main.java` prefix)
- Organized imports
- Convert into Spring Boot Application that can deployed into AWS JavaSE environment (instead of Tomcat environment)
    + Added classes: Application, RegisterController
    + Move webroot under src/main/resources
    + Adjusted jsp mapping and resolutions

##### Installation

- Deploy into ElasticBeanstalk JavaSE environment
- Set Environment Properties (IDENTITY_POOL_ID, DEVELOPER_PROVIDER_NAME, REGION, FIREBASE_DATABASE) via webconsole (under Configuration)
______________________________

# AWSCognitoSampleDeveloperAuthenticationSample

**Overview**

This sample application demonstrates the developer-authenticated functionality of Amazon Cognito. Use this sample in conjunction with the client sample for [Android](../client).

**Prerequisites for use**

- [Create an AWS Account](http://aws.amazon.com/)
- [Install the AWS Mobile SDK](http://aws.amazon.com/mobile/sdk/?nc2=h_l3_ms)
- Read about [developer-authenticated identities](http://docs.aws.amazon.com/cognito/devguide/identity/developer-authenticated-identities/) in the [Amazon Cognito Developer Guide](http://docs.aws.amazon.com/cognito/devguide/)

**Overview of sample setup process**

1. Set up an identity pool that supports developer-authenticated identities.
2. Build the sample yourself and deploy using Amazon Elastic Beanstalk.

**Additional resources**

- **Read** the [Amazon Cognito Developer Guide](http://docs.aws.amazon.com/cognito/devguide/)
- **Read** the [Identity API Reference](http://docs.aws.amazon.com/cognitoidentity/latest/APIReference/Welcome.html)
- **Ask us questions** on the [Amazon Cognito Forums](https://forums.aws.amazon.com/forum.jspa?forumID=173) or open an issue on Github

# Set up an identity pool

**I already have an identity pool set up that supports developer-authenticated identities.**

- Build the sample yourself and deploy using Amazon Elastic Beanstalk.

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


- Build and deploy the server application yourself using Amazon Elastic Beanstalk

1. Create a zip of the content of this folder.
2. Go to the [Elastic Beanstalk console](https://console.aws.amazon.com/elasticbeanstalk), and then click **Create New Application**.
3. Enter an application name, and then click **Next**.
4. Click **Create web server**. The Permissions window appears.
5. Choose the default profile ("aws-elasticbeanstalk-ec2-role"), and then click **Next**.
6. For Predefined Configuration, choose JavaSE. For Environment type, choose Single instance. Click **Next**.
7. For the source, choose the **Upload your own** option and upload the zip file you generated in Step 1. Click **Next**.
8. On the **Environment Information** page, enter an environment name, URL, and description, and then click **Next**.
9. On the **Additional Resources** page, no additional resources are required to configure this sample. Click **Next**.
10. On the **Configuration Details** page, leave the default configuration, and then click **Next**.
11. On the **Environment Tags** page, leave the tags blank. This sample does not require any environment tags. Click **Next**.
12. On the **Review** page, review the details, and then click **Launch**. The console will begin creating an environment. This process will take several minutes.
13. Click **Configuration** , and then click the settings icon to open Software Configuration.
14. Under **Environment properties** , add the following parameters in the custom input field at the bottom of the Property Name column:
	+ Add a parameter named IDENTITY\_POOL\_ID, with the value of identity pool id which you have configured using the Amazon Cognito Console.
	+ Add a parameter named DEVELOPER\_PROVIDER\_NAME, with the developer provider name which you used while creating this identity pool in the Amazon Cognito Console
	+ Add a parameter named REGION with the region to use. Cognito is available in `us-east-1` and `eu-west-1`.
	+ Add a parameter named FIREBASE\_DATABASE with the firebase project ID (available in the settings of your project or in the url)
	+ When finished, click **Save**.
15. Go to the [Identity & Access Management](https://console.aws.amazon.com/iam/) console.
16. Click **Roles**.
17. Select the entry for "aws-elasticbeanstalk-ec2-role."
18. Click **Attach" Policy**.
19. Choose "AmazonCognitoDeveloperAuthenticatedIdentities," and then click **Attach Policy**. Same goes for "AmazonDynamoDBFullAccess"
20. Register users using any web browser using ElasticBeanStalkApplicationURL/jsp/register.jsp. This URL is listed at the top of the Elastic Beanstalk console homepage and ends in ".elasticbeanstalk.com."
21. Get the `firebase.json` file for the server following [these instructions](https://firebase.google.com/docs/admin/setup)

You are now set up to run the sample server application. You can configure the client sample application for [Android](../client) to authenticate registered users using this application.
