#Bonfire

Awesome chat app where you can discuss your favorite emoji. 

## Description

Bonfire is built on top of the new [Firebase][1] from Google. 

You can find the application on the [Play Store][2]. If you are interested in the iOS version, you can find more information about it [here][3].    

## Use the project with your own Firebase instance

1. Clone this repository.

2. Create `signing.properties` file in your `app` folder with the following contents:

    ```
    storeFile=~/android/debug.keystore  # path to your debug.keystore
    storePassword=android
    keyAlias=androiddebugkey
    keyPassword=android
    ```

3. Create a new project in the [Firebase console][4].

4. Click *Add Firebase to your Android app*
  * provide a **unique package name** 
  * use the same package name for the **applicationId** in your `build.gradle`
  * insert SHA-1 fingerprint of your debug certificate, otherwise you won't be able to log in

5. Copy the generated *google-services.json* to the `app` folder of your project.

6. You should be able to successfully sync the project now.

6. Copy contents of the `../server/database.rules.json` into your *Firebase -> Database -> Rules* and publish them.

7. Enable **Google sign-in** in your *Firebase -> Auth -> Sign-in Method*.
 
8. Build and run the app.


[1]: https://firebase.google.com/
[2]: https://play.google.com/store/apps/details?id=com.novoda.bonfire
[3]: https://github.com/novoda/spikes/tree/firebase/develop/Firebase/ios
[4]: https://console.firebase.google.com
