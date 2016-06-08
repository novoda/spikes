## Bonfire

Awesome chat app where you can discuss your favorite emoji.

### Description

Bonfire is built on top of the new Firebase from Google.

You can find the application on the App Store [link]. If you are interested in the Android version, you can find more information about it here [link].

### Requirements
Xcode 7.3
Swift 2.2
Cocoapods 1.0.0

### Set Up

1. Clone this repository
2. Change into the project directory and run
`$ pod install`

3. Create a new project in the [Firebase console](https://console.firebase.google.com/) .
4. Click 'Add Firebase to your iOS App'
    * Provide a iOS Bundle ID (i.e `com.yourapp.ios`)
    * Use the same package name for your bundle identifier in your Xcode project.

5. Download the GoogleService-Info.plist, add it to the root directory of your project, and add it to all targets.


Because this app uses several Firebase features, you'll need to set them up too:

### Database rules
6. Copy contents of the `../server/database.rules.json` into your *Firebase Console -> Database -> Rules* and publish them.

### Google Sign In
7. Enable Google Sign-in the Firebase console: *Firebase Console -> Auth -> Sign-in Method*, and enable the Google sign-in method and click Save.

8. Add custom URL schemes to your Xcode project:
  1. Open your project configuration: double-click the project name in the left tree view. Select your app from the TARGETS section, then select the Info tab, and expand the URL Types section.
  2. Click the + button, and add a URL scheme for your reversed client ID. To find this value, open the GoogleService-Info.plist configuration file, and look for the REVERSED_CLIENT_ID key. Copy the value of that key, and paste it into the URL Schemes box on the configuration page. Leave the other fields blank.
  3. Click the + button, and add a second URL scheme. This one is the same as your app's bundle ID. For example, if your bundle ID is com.example.app, type that value into the URL Schemes box. You can find your app's bundle ID in the General tab of the project configuration (Identity > Bundle Identifier).

  These steps can also be found in the [Firebase Docs](https://firebase.google.com/docs/auth/ios/google-signin#2_implement_google_sign-in)


### Dynamic Links / Invites

9. Firebase console -> Dynamic Links -> Get Started
10. Copy URL at the top of the screen
11. Update the properties in `Firebase Identifiers`


### Done!
7. Build and run the app.
