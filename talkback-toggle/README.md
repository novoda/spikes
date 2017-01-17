# talkback-toggle

We want to be able to turn TalkBack on and off programmatically so we can run connected android tests on them without leaving devices in an inconsistent state.

Run the demo test suite on your device (or emulator) with these commands (TalkBack must be installed on the device first):

```bash
./gradlew core:installDebug; adb shell pm grant com.novoda.toggletalkback android.permission.WRITE_SECURE_SETTINGS; adb shell am start -a "com.novoda.toggletalkback.DISABLE_TALKBACK"; ./gradlew demo:cDebugAT; adb shell am start -a "com.novoda.toggletalkback.DISABLE_TALKBACK";
```

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

## Using for Espresso tests

- install core debug apk - this includes the activity that can turn TalkBack on/off
- grant apk system permission to write setting (set flag for a11y service on/off)
- disable TalkBack
- run connected android tests for demo
- disable TalkBack (just in case the tests crashed)

