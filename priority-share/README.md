# priority-share [![](https://raw.githubusercontent.com/novoda/novoda/master/assets/btn_apache_lisence.png)](LICENSE.txt)

Probably the easiest way to share something in Android.


## Description

priority-share allows you to implement a priority share action, that is, presenting the user with a set of predefined apps that you can define
to share to, as a first "quick dial" experience, and offering a "show more" to allow the user to choose other, non-prioritised apps to share to.

The `PrioritySharer.Builder` class makes it really easy to customise the bits you want, such as give priority to a specific type of apps such as
Twitter clients. You can also modify the mime type and add a (`Uri`)[https://developer.android.com/reference/android/net/Uri.html] that contains the
data you want to send. In addition you can fine-tune the generated (`Intent`)[https://developer.android.com/reference/android/content/Intent.html]
and add your own flags, extras, etc. providing a `OnPrepareSharingIntentListener`.


## Adding to your project

To start using this library, add these lines to the `build.gradle` of your project:

```groovy
repositories {
    jcenter()
}
dependencies {
    // TODO: To be done once this is released.
}
```


## Simple usage

Sharing a string is as easy as this:

```java
new PrioritySharer.Builder()
        .setText("PriorityShare allows you to share stuff in such an easy way!")
        .setTargets(TargetApps.TWITTER) // Other options available: FACEBOOK, GOOGLE_PLUS or even a custom set of apps
        .show(context);
```


## Links

Here are a list of useful links:

 * We always welcome people to contribute new features or bug fixes, [here is how](https://github.com/novoda/novoda/blob/master/CONTRIBUTING.md)
 * If you have a problem check the [Issues Page](https://github.com/novoda/TODO-project-name/issues) first to see if we are working on it
 * For further usage or to delve more deeply checkout the [Project Wiki](https://github.com/novoda/TODO-project-name/wiki)
 * Looking for community help, browse the already asked [Stack Overflow Questions](http://stackoverflow.com/questions/tagged/support-priority-share) or use the  tag: `priority-share` when posting a new question
