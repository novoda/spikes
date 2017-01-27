# talkback-toggle [![Build](https://ci.novoda.com/buildStatus/icon?job=talkback-toggle)](https://ci.novoda.com/job/talkback-toggle/lastBuild/console) [![Download](https://api.bintray.com/packages/novoda/maven/talkback-toggle/images/download.svg)](https://bintray.com/novoda/maven/talkback-toggle/_latestVersion) [![License](https://raw.githubusercontent.com/novoda/novoda/master/assets/btn_apache_lisence.png)](LICENSE.txt)

Turn Android Talkback on and off simply by sending an intent!

## Description

We want to be able to turn TalkBack on and off programmatically so we can run connected Android tests on them without leaving devices in an inconsistent state.

- `TalkBackStateSettingActivity` toggles the service on and off. It responds to an intent (see below), after permission has been granted on the app.
- `TalkBackActivityTestRule` (from `talkback-toggle-espresso`) can be used in your espresso tests to automatically enable TalkBack for each test and disable after

## Adding to your project

To start using this library, add these lines to the `build.gradle` of your project:

```groovy
repositories {
    jcenter() // not on jcenter yet
}

android {
    testBuildType "espresso" // you'll need a build type for your connected android tests
}

dependencies {
    espressoCompile 'com.novoda:talkback-toggle:<latest-version>' // the core library
    androidTestCompile 'com.novoda:talkback-toggle-espresso:<latest-version>' // if you need the TalkBackActivityTestRule
}
```

## Simple usage

The library works by modifying system settings which means you'll need to set the permission on your app using ADB. Assuming the package name for the `espresso` build above is `com.novoda.movies`, you can set the permissions as follows:

```bash
adb shell pm grant com.novoda.movies android.permission.WRITE_SECURE_SETTINGS
```

You can enable/disable TalkBack via adb with the following actions:

```bash
$ adb shell am start -a "com.novoda.talkbacktoggle.ENABLE_TALKBACK"
$ adb shell am start -a "com.novoda.talkbacktoggle.DISABLE_TALKBACK"
```

You can also do the same with an Intent:

```java
Intent intent = new Intent("com.novoda.talkbacktoggle.ENABLE_TALKBACK")
context.startActivity(intent);
```

## Demo

You can run the demo tests with the following commands:

```bash
./gradlew demo:installEspresso; adb shell pm grant com.novoda.movies android.permission.WRITE_SECURE_SETTINGS; adb shell am start -a "com.novoda.talkbacktoggle.DISABLE_TALKBACK"; ./gradlew demo:cAT; adb shell am start -a "com.novoda.talkbacktoggle.DISABLE_TALKBACK";
```

You have to install the app first to set the permission. We also disable TalkBack before and after the tests so we don't mess up non-TalkBack tests.

## Links

Here are a list of useful links:

 * We always welcome people to contribute new features or bug fixes, [here is how](https://github.com/novoda/novoda/blob/master/CONTRIBUTING.md)
 * If you have a problem check the [Issues Page](https://github.com/novoda/talkback-toggle/issues) first to see if we are working on it // TODO: waiting on repo to exist
