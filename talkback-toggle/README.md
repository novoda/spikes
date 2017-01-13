talkback-toggle
=============
We want to be able to turn TalkBack on and off programmatically so we can run connected android tests on them without leaving devices in an inconsistent state.

Try it out:

```bash
./gradlew assembleDebug; adb install mobile/build/outputs/apk/mobile-debug.apk; adb shell pm grant com.novoda.toggletalkback android.permission.WRITE_SECURE_SETTINGS; adb shell am start -a "com.novoda.toggletalkback.DISABLE_TALKBACK"; ./gradlew connectedDebugAndroidTest
```

Compile the APK:

```bash
./gradlew assembleDebug;
```

Install the APK:

```bash
adb install mobile/build/outputs/apk/mobile-debug.apk;
```

Re-set the permission to allow the app to enable/disable TalkBack by writing to the secure settings:

```bash
adb shell pm grant com.novoda.toggletalkback android.permission.WRITE_SECURE_SETTINGS;
```

Disable TalkBack before we begin, in case we have tests for non-TalkBack stories in our suite. The TalkBackActivityTestRule ought to turn the service on/off for each test.

```bash
adb shell am start -a "com.novoda.toggletalkback.DISABLE_TALKBACK"
```

Run the connected tests:

```bash
./gradlew connectedDebugAndroidTest
```

Disable TalkBack at the end, in case the tests crash, we don't want to leave TalkBack on for a test device
```bash
adb shell am start -a "com.novoda.toggletalkback.DISABLE_TALKBACK"
```

---

You can enable and disable TalkBack with the following actions:

```bash
$ adb shell am start -a "com.novoda.toggletalkback.ENABLE_TALKBACK"
$ adb shell am start -a "com.novoda.toggletalkback.DISABLE_TALKBACK"
```

or with an Intent, as is done in the `TalkBackActivityTestRule`:

```java
Intent intent = new Intent("com.novoda.toggletalkback.ENABLE_TALKBACK")
context.startActivity(intent);
```
