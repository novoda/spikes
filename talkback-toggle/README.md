talkback-toggle
=============
We want to be able to turn TalkBack on and off programmatically so we can run connected android tests on them without leaving devices in an inconsistent state.

You need to install the app, then enable the permission to write secure settings:

```bash
$ adb shell pm grant com.novoda.toggletalkback android.permission.WRITE_SECURE_SETTINGS
```

DONE:
- create debug app to toggle TalkBack with buttons in-app
- test behaviour on non-rooted physical device (works!)
- check behaviour on device without TalkBack, install TalkBack, see if wizard can be skipped if enabled programmatically.
No luck, it opens the TalkBack tutorial but you can send the back key event via adb (`adb shell input keyevent 4`)

TODO:
- create espresso actions to simulate TalkBack gestures
- create dummy espresso test showcasing TalkBack test
- add script to install app, enable TalkBack, run tests, then disable TalkBack
- modify script to revert TalkBack state to whatever it was before
- modify settings edit so that only TalkBack is affected (other accessibility services do not change state)
- modify script to install TalkBack apk if not present