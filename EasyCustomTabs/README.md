# EasyCustomTabs [![](https://ci.novoda.com/buildStatus/icon?job=landing-strip)]() [![](https://raw.githubusercontent.com/novoda/novoda/master/assets/btn_apache_lisence.png)]()

^^^ **TODO** link properly

- Navigating to external websites from your app? 
- *"Yes, that one is easy"*
- Wondering how to keep the user in context?
- *"I know... it can be done using WebView, but I don't like it that much"*
- Aha! Why don't you try Chrome Custom Tabs? 
- *"Integrating it into my app can be a pain..."*
- Not anymore!

**EasyCustomTabs** will help you with that.

## Description

**EasyCustomTabs** provides easy integration of Chrome Custom Tabs into your project.
Just connect it to your activity, and navigate to the external website styling your tab as you wish.
Moreover with Custom Tabs the navigation can be nearly instantaneous!

More information about Chrome Custom Tabs available at: https://developer.chrome.com/multidevice/android/customtabs

## Adding to your project

To start using this library, add these lines to the `build.gradle` of your project:

**TODO**: Move to own repo + bintray at some point in the future.


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

**TODO**: Add screenshot.

Info about customizing your tab and more stuff into [Github Wiki]() **TODO**: Add wiki and link.



## Links

Here are a list of useful links:

 * We always welcome people to contribute new features or bug fixes, [here is how](https://github.com/novoda/novoda/blob/master/CONTRIBUTING.md)
 * If you have a problem check the [Issues Page]() first to see if we are working on it <- **TODO**: Add link
 * For further usage or to delve more deeply checkout the [Project Wiki]() <- **TODO**: Add link
