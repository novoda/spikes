# Android build properties plugin
**TL;DR** A Gradle plugin to add easy support of property files to your Android build.<br/>
<br/>

Sometimes you want to retrieve some information from a file that is not checked in as part of your repo for security reasons (keys, credentials, passwords, etc).
Other times it's just you already are given a properties file and you need to make sure it ends up in your application `BuildìConfig` or in some resource file.
This plugin augments the `android` extension, aiming to provide a simple way to:
- define handles to properties files in your build script (à la `signingConfig`)
- define a string field in your `BuildConfig` filled with a value in a properties file
- load an entire properties file into your `BuildConfig`
- use properties files for signing configurations avoiding to worry about passwords and keystores in your repo

As a bonus the plugin enhances the `buildConfigField` and `resValue` facilities to enforce types.

| Before | After |
|----|----|
|```buildConfigField 'String', 'LOL', '\"sometimes the picture takes\"'``` | ```buildConfigString 'LOL', 'sometimes the picture takes'```|


### How To

To build the latest version of the plugin, run from the root of the repo:
```
./gradlew build
```

The plugin at the moment is not deployed in any public maven repo, therefore your only option is to deploy it to a local one.
A default setup with an embedded repo (pointing to `.gradle/repo`) is provided, just run the following command from the root
 of the project:
```
./gradlew -Plocal uploadArchives
```

The sample app is not part of the main project to work around the fact the plugin might be not deployed yet either locally or remotely.
To give it a go you need to **change your working directory to `sample/`**, and build from there. The same `-Plocal` flag is supported
by the sample app to use the embedded local repo to retrieve the plugin:

```
./gradlew -Plocal assemble
```


