# monkey-runner [![](https://ci.novoda.com/buildStatus/icon?job=monkey-runner-plugin)](https://ci.novoda.com/job/monkey-runner-plugin/lastBuild/console) [![](https://raw.githubusercontent.com/novoda/novoda/master/assets/btn_apache_lisence.png)](LICENSE.txt)

Provides gradle closure to configure the monkey runner.


## Description

Decorates the [Android monkey](https://developer.android.com/studio/test/monkey.html) with the [monkey trap](https://github.com/novoda/spikes/tree/master/MonkeyTrap/),
 which is designed to block the monkey from accessing the system notifications tray (and the quick toggles).

## Adding to project

In case you want the monkey trap running, it's necessary to have it installed on all the devices you want to use 
monkey runner with. You can easily ensure this with:

```bash
wget https://raw.githubusercontent.com/novoda/spikes/master/MonkeyTrap/apk/app-debug.apk
for SERIAL in $(adb devices | tail -n +2 | cut -sf 1);
do
  adb -s $SERIAL install app-debug.apk
done
rm app-debug.apk
```

You can run this as part of your CI job before starting the monkey runner.

In your Android module's `build.gradle`, add this plugin as a dependency, apply it and configure it. Be sure you also apply
 the `android-command` plugin (which `monkey-runner` depends on):

```groovy
buildscript {
    repositories {
        ...
        maven {
            url 'http://dl.bintray.com/novoda/maven'
        }
    }

    dependencies {
        ...
        classpath 'com.novoda:monkey-runner:<latest-version>'
    }
}

...

apply plugin: 'com.novoda.android-command'
apply plugin: 'com.novoda.monkey-runner'

...

monkeyRunner {
    taskDependency 'installDebug'
    eventsCount 50000
    packageNameFilter 'com.example.app'
    logFileName 'monkey_events.log'
}
```

## Configuration

You can configure the following properties on the `monkeyRunner` extension:
- `taskDependency`: task to be run before the monkey runner starts (usually this is an install task for your app)
- `eventsCount` (optional): number of events executed by the monkey runner (**default: 50000**)
- `packageNameFilter`: your app package name
- `logFileName` (optional): name of the monkey runner log (**default: monkey.log**)
- `useMonkeyTrap` (optional): whether to use the monkey trap or not (**default: true**)


## Simple usage

Run the following to start the monkey on all connected devices:

```bash
./gradlew runMonkeyAll
```

## Links

Here are a list of useful links:

 * We always welcome people to contribute new features or bug fixes, [here is how](https://github.com/novoda/novoda/blob/master/CONTRIBUTING.md)
 * TODO: If you have a problem check the [Issues Page](https://github.com/novoda/TODO/issues) first to see if we are working on it
 * TODO: Looking for community help, browse the already asked [Stack Overflow Questions](http://stackoverflow.com/questions/tagged/TODO) or use the tag: `TODO` when posting a new question
