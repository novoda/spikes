## Bonfire

Awesome chat app where you can discuss your favorite emoji.

### Description

Bonfire is built on top of the new Firebase from Google.

You can find the application on the App Store [link]. If you are interested in the Android version, you can find more information about it here [link].

### Requirements
* Xcode 7.3
* Swift 2.2
* Cocoapods 1.0.0


----

## Set Up
Want to run your own version of the app? Go ahead!

1. Clone this repository
2. Change into the project directory and run
`$ pod install`

3. Create a new project in the [Firebase console](https://console.firebase.google.com/) .
4. Click 'Add Firebase to your iOS App'
    * Provide a iOS Bundle ID (i.e `com.yourapp.ios`)
    * Use the same package name for your bundle identifier in your Xcode project.

5. Download the GoogleService-Info.plist, add it to the root directory of your project, and add it to all targets.


Because this app uses several Firebase features, you'll need to set them up too:

##### Database rules
6. Copy contents of the `../server/database.rules.json` into your *Firebase Console -> Database -> Rules* and publish them.

##### Google Sign In
1. Enable Google Sign-in the Firebase console: *Firebase Console -> Auth -> Sign-in Method*, and enable the Google sign-in method and click Save.

Next, add custom URL schemes to your Xcode project:

1. Open your project configuration: double-click the project name in the left tree view. Select your app from the TARGETS section, then select the Info tab, and expand the URL Types section.

2. Click the + button, and add a URL scheme for your reversed client ID. To find this value, open the GoogleService-Info.plist configuration file, and look for the REVERSED_CLIENT_ID key. Copy the value of that key, and paste it into the URL Schemes box on the configuration page. Leave the other fields blank.

3. Click the + button, and add a second URL scheme. This one is the same as your app's bundle ID. For example, if your bundle ID is com.example.app, type that value into the URL Schemes box. You can find your app's bundle ID in the General tab of the project configuration (Identity > Bundle Identifier).

  These steps can also be found in the [Firebase Docs](https://firebase.google.com/docs/auth/ios/google-signin#2_implement_google_sign-in)


##### Dynamic Links / Invites

1. Firebase console -> Dynamic Links -> Get Started
2. Copy URL at the top of the screen
3. Update the properties in `Firebase Identifiers`


### Done!

You should now be able to build, test and run the app.

## Links

* [Novoda blog link]
* [Firebase Developer Docs](https://firebase.google.com/docs/)
* [What iOS Developers Should Know About the Firebase Platform - Google I/O 2016](https://www.youtube.com/watch?v=L2LZKxdzY3g)
