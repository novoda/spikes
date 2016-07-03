# Android build properties plugin
**TL;DR** A Gradle plugin to add easy support of property files to your Android build.<br/>
<br/>

Sometimes you want to retrieve some information from a file that is not checked in as part of your repo for security reasons (keys, credentials, passwords, etc).
Other times it's just you already are given a properties file and you need to make sure it ends up in your application `BuildConfig` or in some resource file.<br/>
This plugin augments the `android` default facilities, aiming to provide a simple way to:
- define handles to properties file in your build script (à la `signingConfig`)
- generate string fields in your `BuildConfig` with values from a properties file
- generate string resources with values from a properties file
- load signing configurations from properties files avoiding to worry about passwords and keystores references in your build script

## How To Build

To build the latest version of the plugin, run from the root of the repo:
```
./gradlew build
```

The plugin at the moment is not deployed in any public maven repo, therefore your only option is to deploy it to a local one.
A default setup with an embedded repo (pointing to `.gradle/repo`) is provided, just run the following command from the root
 of the project:
```
./gradlew deployLocal
```

The sample app is not part of the main project to work around the fact the plugin might be not deployed yet either locally or remotely.
To give it a go you need to **change your working directory to `sample/`**, and build from there. The sample app by default will use the embedded local repo to retrieve the plugin:
```
./gradlew assemble
```

## How To Use

#### 1.Maven coordinates
The plugin **is not deployed on any public maven repo at the moment**, therefore you have the only choice of using it in
the sample app via the embedded local repo. The setup for this scenario is already in place (Fear not, Bintray deploy is coming soon).

#### 2. Apply plugin
The plugin needs to be applied to your Android module after the Android gradle plugin. In your `build.gradle` you should put
```
apply plugin: 'build-properties'
```
#### 3. List properties files
In your android project you can add a `buildProperties` configuration listing all the properties files you intend
to reference in your `android` configuration, eg:
```
buildProperties {
    secrets {
        file project.file('secrets.properties')
    }
}

android {
...
}
```

## Features

#### 1. Store whole properties file in `BuildConfig`
In any product flavor configuration (or `defaultConfig`) you can use `buildConfigProperties` as follows:

```
    defaultConfig {
        ...
        buildConfigProperties buildProperties.secrets
        ...
    }
```
All the entries in that properties files are converted to string fields in your `BuildConfig`. Names of the fields are
created formatting the key of each entry to follow the standard constant field naming in Java (uppercase with underscores), eg:

`apiKey` -> `API_KEY`<br/>
`api_key` -> `API_KEY`<br/>
`api.key` -> `API_KEY`<br/>

Note that properties files treat values as strings, therefore only string fields are generated in this case.

#### 2. Store a property value into your `BuildConfig`
In any product flavor configuration (or `defaultConfig`) you can use `buildConfigProperty` as follows:
```
    defaultConfig {
        ...
        buildConfigProperty 'API_KEY', buildProperties.secrets['apiKey']
        ...
    }
```
You can omit the field name and let the plugin generate one for you (following the same rules at 1.)

#### 3. Store whole properties file as generated string resources
In any product flavor configuration (or `defaultConfig`) you can use `resValueProperties` as follows:

```
    defaultConfig {
        ...
        resValueProperties buildProperties.secrets
        ...
    }
```
All the entries in that properties files are converted to string resources, named after their key, enforcing snake casing over camel casing if necessary.


#### 4. Store a property value as generated string resource
In any product flavor configuration (or `defaultConfig`) you can use `resValueProperty` as follows:

```
    defaultConfig {
        ...
        resValueProperty 'api_key', buildProperties.secrets['apiKey']
        ...
    }
```
You can omit the name of the resource and let the plugin generate one for you from the property key, enforcing snake casing over camel casing if necessary.


#### 5. Load signing configuration from properties
Instead of inline your passwords and other details in your build script you can fill the signing configuration using a properties file.
```
signingConfigs {
  release {
    signingConfigProperties buildProperties.releaseSigning
  }
}
```
The plugin will automatically retrieve all the needed fields from the properties file.
Note: the path of the keystore file is considered relative to the path of the specified properties file.

## Bonus

#### Typed `buildConfigField`/`resValue`
The plugin enhances the `buildConfigField` and `resValue` facilities to enforce types.
To generate a string field in your `BuildConfig` you used to write:
```
buildConfigField 'String', 'LOL', '\"sometimes the picture take\"'
```
but now you can instead write:
```
buildConfigString 'LOL', 'sometimes the picture take'
```
The full list of new typed facilities is as follows:

| | Example |
|----|----|
|`buildConfigBoolean` | `buildConfigBoolean 'TEST_BOOLEAN', false`|
|`buildConfigInt` | `buildConfigInt 'TEST_INT', 42`|
|`buildConfigLong` | `buildConfigLong 'TEST_LONG', System.currentTimeMillis()`|
|`buildConfigDouble` | `buildConfigDouble 'TEST_DOUBLE', Math.PI`|
|`buildConfigString` | `buildConfigString 'TEST_STRING', "whateva"`|
|`resValueInt`| `resValueInt 'debug_test_int', 100`|
|`resValueBoolean` | `resValueBoolean 'debug_test_bool', true`|
|`resValueString` | `resValueString 'debug_test_string', 'dunno bro...'`|

#### More on load properties
Properties files are lazy-loaded when accessing some of their properties for the first time in the build script. Given a `BuildProperties` instance one of its entries can be retrieved using the `getAt` operator:

`BuildProperty.Entry entry = buildProperties.secrets['aProperty']`

The value of an entry can be retrieved via one of the following typed accessors:

- `entry.getBoolean()`, or `entry.boolean`
- `entry.getInt()`, or `entry.int`
- `entry.getDouble()`, or `entry.double`
- `entry.getString()`, or `entry.string`

If you want to access the raw value (`Object`) you can also use `entry.getValue()`/`entry.value`. Is important to note that values are lazily accessed too (via the internal closure provided in `Entry`).
Trying to access the value of a specific property could generate an exception if the key is missing in the provided properties file, eg:
```
FAILURE: Build failed with an exception.

* What went wrong:
A problem occurred configuring project ':app'.
> No value defined for property 'notThere' in 'secrets' properties (/Users/toto/novoda/spikes/BuildPropertiesPlugin/sample/properties/secrets.properties)

```
