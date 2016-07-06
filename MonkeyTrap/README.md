# Monkey Trap

A way to prevent the Android UI/Application Exerciser Monkey from accessing the System Notifications tray. This makes sure the Monkey doesn't leave a device without network connectivity after it finishes.

The code is based on https://github.com/Orange-OpenSource/simiasque and http://www.piwai.info/chatheads-basics/


### Installing

Either download the APK from Github // TODO: Add link

Or from the terminal:

```bash
./gradlew clean installDebug
```

### How to use

From an Android application:

```java
Intent intent = new Intent("com.novoda.monkeytrap.SHOW_OVERLAY")
        .setPackage("com.novoda.monkeytrap")
        .putExtra("show", true); // Will show the overlay
        // .putExtra("show", false); // Will hide the overlay
startService(intent);
```

From a terminal:

```bash
adb shell am startservice -a com.novoda.monkeytrap.SHOW_OVERLAY --ez show true # Will show the overlay

adb shell am startservice -a com.novoda.monkeytrap.SHOW_OVERLAY --ez show false # Will hide the overlay
```
