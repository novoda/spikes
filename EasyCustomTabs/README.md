# EasyCustomTabs 

TODO - Adding to CI + Licence at some point in the future.

Navigating to external websites from your app? 

Wondering how to keep the user in context?

**EasyCustomTabs** will help you with this.

## Description

**EasyCustomTabs** provides easy integration of Chrome Custom Tabs into your project.
Just connect it to your activity, and navigate to the external website styling your tab as you wish!

More information about Chrome Custom Tabs available at: https://developer.chrome.com/multidevice/android/customtabs

## Adding to your project

To start using this library, add these lines to the `build.gradle` of your project:

TODO - move to own repo + bintray at some point in the future.


## Simple usage

1) Initialize `EasyCustomTabs`, you only have to this once.

```java
EasyCustomTabs.initialize(context)
```

2) Connect `EasyCustomTabs` to your `Activity` as soon as it is resumed.

```java
public void onResume() {
    super.onResume()
    EasyCustomTabs.getInstance().connectTo(this);
}
```

And don't forget to disconnect when the `Activity` is paused.

```java
public void onPause() {
    EasyCustomTabs.getInstance().disconnectFrom(this);
    super.onPause();
}
```

3) Navigate!

```java
Uri url = ANY_WEBSITE_URL;
EasyCustomTabs.getInstance().navigateTo(url, activity);
```

TODO - add screenshot.

Info about customizing your tab and more stuff into [Github Wiki] TODO - add wiki



## Links

Here are a list of useful links:

 * We always welcome people to contribute new features or bug fixes, [here is how](https://github.com/novoda/novoda/blob/master/CONTRIBUTING.md)
 * If you have a problem check the [Issues Page](https://github.com/novoda/landing-strip/issues) first to see if we are working on it
 * For further usage or to delve more deeply checkout the [Project Wiki](https://github.com/novoda/landing-strip/wiki)
