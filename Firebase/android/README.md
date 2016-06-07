#Bonfire

Awesome chat app where you can discuss your favorite emoji. 

## Description

Bonfire is built on top of the new [Firebase][1] from Google. 

You can find the application on the [Play Store][2]. If you are interested in the iOS version, you can find more information about it [here][3].    

## Use the project with your own Firebase instance

1. Clone this repository
2. Create `signing.properties` file in your `app` folder with the following contents
    ```
    storeFile=~/android/debug.keystore  # path to your debug.keystore
    storePassword=android
    keyAlias=androiddebugkey
    keyPassword=android
    ```
3. Create a new project in the [Firebase console][4]
4. Click *Add Firebase to your Android app*
  * provide a unique package name (and use the same package name as *applicationId* in your `build.gradle`)
  * copy *google-services.json* to the `app` folder of your project
5. Copy contents of the `database.rules.json` into your Firebase Database Rules and publish them
6. Enable Google sign-in in Firebase -> Auth -> Sign-in Method 


[1]: https://firebase.google.com/
[2]: https://play.google.com/store/apps/details?id=com.novoda.bonfire
[3]: https://github.com/novoda/spikes/tree/firebase/develop/Firebase/ios
[4]: https://console.firebase.google.com
