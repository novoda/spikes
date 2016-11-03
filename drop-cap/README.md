# DropCap [![](https://ci.novoda.com/buildStatus/icon?job=drop-cap)](https://ci.novoda.com/job/drop-cap/lastBuild/console) [![Download](https://api.bintray.com/packages/novoda/maven/drop-cap/images/download.svg) ](https://bintray.com/novoda/maven/drop-cap/_latestVersion) [![](https://raw.githubusercontent.com/novoda/novoda/master/assets/btn_apache_lisence.png)](LICENSE.txt)

This library can be used to create a DropCap in your app!

![drop_cap](https://cloud.githubusercontent.com/assets/3380092/12559924/bf2a7ff2-c38f-11e5-927f-c2b7e4993390.png)

## Description

This library can be used to create a DropCap in your app! A DropCap is the first letter of a paragraph that's of a much
bigger size than the rest that follow. The formatting is such that the DropCap spans (drops down) to cover the few lines
following the first.

## Adding to your project

To start using this library, add these lines to the `build.gradle` of your project:

```groovy
repositories {
    jcenter()
}

dependencies {
    compile 'com.novoda:drop-cap:<latest-version>'
}
```

## Simple usage

Simply add `DropCapView` to one of your layout xml files and use the following xml attributes for customization.

```
  <attr name="lineSpacingExtra" format="dimension" />
  <attr name="dropCapTextSize" format="dimension" />
  <attr name="dropCapTextColor" format="color" />
  <attr name="dropCapFontPath" format="string" />

  <attr name="copyTextSize" format="dimension" />
  <attr name="copyTextColor" format="color" />
  <attr name="copyFontPath" format="string" />
```

For ease of use the `DropCapView` uses the `android:text` attribute to specify text via the xml layout.


For further usage or to delve more deeply checkout the associated [`sample`](https://github.com/novoda/spikes/tree/master/drop-cap/sample) app. 

## Links

Here are a list of useful links:

 * We always welcome people to contribute new features or bug fixes, [here is how](https://github.com/novoda/novoda/blob/master/CONTRIBUTING.md)
 * If you have a problem check the [Issues Page](https://github.com/novoda/drop-cap/issues) first to see if we are working on it
