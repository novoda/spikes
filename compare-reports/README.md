# compare-reports [![](http://ci.novoda.com/buildStatus/icon?job=compare-reports)](http://ci.novoda.com/job/compare-reports/lastBuild/console) [![](https://raw.githubusercontent.com/novoda/novoda/master/assets/btn_apache_lisence.png)](LICENSE.txt)

Tells you how many issues you've introduced/fixed to the static analysis reports locally compared to the main branch of the project.


## Description

This is WIP so // TODO
For now only the comparison of checkstyle reports work, more to come soon.


## Adding to your project

To start using this library, add these lines to the `build.gradle` of your project:

```groovy
apply plugin: 'compare-reports'

buildscript {
    repositories {
        maven {
            credentials {
                username 'BINTRAY_USERNAME'
                password 'BINTRAY_KEY'
            }
            url 'http://dl.bintray.com/novoda/maven-private'
        }
    }
    dependencies {
        classpath 'com.novoda:compare-reports:0.0.2-beta'
    }
}
```


## Simple usage

You can configure the plugin to use a specific branch in git as a master or main branch and you have to specify the regex that will be used to locate the report files:

```groovy
compareReports {
    mainBranchName 'master' // If not specified, it will use git's default one
    checkstyleFiles '**/reports/checkstyle/*.xml'
}
```

Now you can get the list of issues you've introduced by running `gradle compareReports`. You can also add a parameter to see the issues you've fixed `gradle compareReports -PshowFixed`.


## Links

Here are a list of useful links:

 * We always welcome people to contribute new features or bug fixes, [here is how](https://github.com/novoda/novoda/blob/master/CONTRIBUTING.md)
 * If you have a problem check the [Issues Page](https://github.com/novoda/spikes/issues) first to see if we are working on it
 * For further usage or to delve more deeply checkout the [Project Wiki](https://github.com/novoda/spikes/wiki)
 * Looking for community help, browse the already asked [Stack Overflow Questions](http://stackoverflow.com/questions/tagged/support-compare-reports) or use the tag: `support-compare-reports` when posting a new question
