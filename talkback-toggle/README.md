talkback-toggle
=============
We want to be able to turn TalkBack on and off programmatically so we can run connected android tests on them without leaving devices in an inconsistent state.

You need to install the app, then enable the permission to write secure settings:

```bash
$ adb shell pm grant com.novoda.toggletalkback android.permission.WRITE_SECURE_SETTINGS
```
