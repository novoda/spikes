# talkback-toggle

// these instructions are a little out of date..
// basically, the espresso test demos are in the demo module, and everything else in core.
// build core, install apk to device, set the permission, then run the espresso tests from demo

We want to be able to turn TalkBack on and off programmatically so we can run connected android tests on them without leaving devices in an inconsistent state.

Run the demo test suite on your device (or emulator) with these commands (TalkBack must be installed on the device first):

```bash
./gradlew assembleDebug; adb install mobile/build/outputs/apk/mobile-debug.apk; adb shell pm grant com.novoda.toggletalkback android.permission.WRITE_SECURE_SETTINGS; adb shell am start -a "com.novoda.toggletalkback.DISABLE_TALKBACK"; ./gradlew connectedDebugAndroidTest
```

## Individual steps

### Compile APK

```bash
./gradlew assembleDebug;
```

### Install APK

```bash
adb install mobile/build/outputs/apk/mobile-debug.apk;
```

### Set permission

After the app is installed, we need to set the permission to allow the app to write to secure settings. This is because accessibility services are turned on/off with flags in the secure settings.

```bash
adb shell pm grant com.novoda.toggletalkback android.permission.WRITE_SECURE_SETTINGS;
```

### Disable TalkBack

Although we'll be using the `TalkBackActivityTestRule` for test suites that require TalkBack enabled, we may have other tests (using `ActivityTestRule`) that relies on TalkBack _not_ being enabled. Since it makes more sense to use the default `ActivityTestRule` rather than creating `NoTalkBackActivityTestRule` for tests that don't include TalkBack, we will also disable TalkBack from adb before we begin the tests:

```bash
adb shell am start -a "com.novoda.toggletalkback.DISABLE_TALKBACK"
```

### Run the connected tests

```bash
./gradlew connectedDebugAndroidTest
```

### Disable TalkBack (again)

Although our `TalkBackActivityTestRule` will disable TalkBack, the tests could crash, leaving TalkBack on. We should manually disable it in case it messes up test runs for other test suites running on the same device. The test rule is still useful so we can toggle it on and off between tests in our own test suite.


```bash
adb shell am start -a "com.novoda.toggletalkback.DISABLE_TALKBACK"
```

---

As you've read above, you can enable/disable TalkBack via adb with the following actions:

```bash
$ adb shell am start -a "com.novoda.toggletalkback.ENABLE_TALKBACK"
$ adb shell am start -a "com.novoda.toggletalkback.DISABLE_TALKBACK"
```

You can also do the same with an Intent, as is done in the `TalkBackActivityTestRule`:

```java
Intent intent = new Intent("com.novoda.toggletalkback.ENABLE_TALKBACK")
context.startActivity(intent);
```
