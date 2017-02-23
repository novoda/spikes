Running the Custom Authentication Sample
============================================
This is a fork of [aws-sdk-android-samples/CognitoSyncDemo](https://github.com/awslabs/aws-sdk-android-samples/tree/master/CognitoSyncDemo). It has been quite heavily modified to remove what we were not interested in, add Firebase support and bringing it to an acceptable level in terms of code quality.
For the client to work you also need the [server](../server).

This sample demonstrates how to fetch tokens from Cognito and Firebase on Android in order to access restricted resources (respectively, lambdas and database).
It supports Cognito developer authenticated identities as well as Firebase (using the custom authentication system).

1. Import the client project into Android Studio
      * From the Welcome screen, click on "Import project".
      * Browse to the client directory and press OK.
	  * Accept the messages about adding Gradle to the project.
	  * If the SDK reports some missing Android SDK packages (like Build Tools or the Android API package), follow the instructions to install them.

2. Update your App configuration for Cognito:
   * Make sure you have [created and configured an identity pool](https://console.aws.amazon.com/cognito/) and you downloaded the starter code at the last step of the wizard.
   * Create a `secret.properties` file at the root and paste:
   ```
   identity_pool=
   region=
   developer_provider=
   authentication_endpoint=
   ```
   * Set the `identity_pool` and `region` with the values from the starter code.
   * Set the `authentication_endpoint` received from the Amazon ElasticBeanStalk console.
   * Set the `developer_provider` to the one you set in Amazon Cognito console for your identity pool.

3. Create an Amazon resource accessible only for authenticated users
   * Create a lambda named `CognitoAuthTest` (we choosed the hello-world / nodejs blueprint) and paste the following so when calling this lambda you get `Server say hello and welcome!` as a result
       ```
       'use strict';

       console.log('Loading function');

       exports.handler = (event, context, callback) => {
           callback(null, 'Server say hello and welcome!');
       };
       ```
       Role should be `lambda_basic_execution` as we don't need anything else
   * Go then to IAM -> Roles -> *_Auth_role (the role corresponding to authenticated users for your identitiy pool) and edit the Policy
   * Under Statement, add this bit which allow access to our authenticated user to the lambda we just created
       ```
       {
           "Effect": "Allow",
           "Action": [
               "lambda:InvokeFunction"
           ],
           "Resource": [
               "arn:aws:lambda:YOUR_REGION:YOUR_ACCOUND_ID:function:CognitoAuthTest"
           ]
       }
       ```
       Dont forget to replace YOUR_REGION and YOUR_ACCOUND_ID (that you can find under support in the top bar -> support center)

4. Update your App configuration for Firebase:
   * Create a Firebase project in the [Firebase console](https://console.firebase.google.com/) by clicking on Create New Project.
   * Click Add Firebase to your Android app and follow the setup steps.
   * When prompted, enter `com.amazonaws.cognito.sync.demo` for the app package name.
   * At the end, you'll download a google-services.json file and put it in the root folder.

5. Create a Firebase resource accessible only for authenticated users:
   * In the [Firebase console](https://console.firebase.google.com/), go to the Database section and a child named `test_reading` with the value you want. The value is going to be read when trying to access the Firebase resource. If you go to the RULES tab you can see the database is only accessible to authenticated users by default.

6. Run the app:
   * Trying to access resources when you are not logged in to your server is going to yield an error
   * Once logged using the login button (don't forget to add a user! See the server README of this sample) you can then access firebase and amazon resources (it will also fetch and cache the corresponding token before doing so)
   * The wipe data button will disconnect cognito and firebase and wipe the shared preferences (except the device UID)
