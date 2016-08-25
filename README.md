# Android build properties plugin

Sometimes it's necessary to retrieve some information from a properties 
file that is not checked in as part of your repo for security reasons 
(keys, credentials, passwords, etc). Such properties need to end up in 
your application `BuildConfig` or in some resource file.

This plugin aims to provide a simple way to:
- define handles to properties file in your build script (à la `signingConfig`)
- generate fields in your `BuildConfig` with values from a properties file
- generate resources with values from a properties file
- load signing configurations from a properties file


## How To Use

The plugin is deployed to Bintray's JCenter. Ensure it's correctly defined
as dependency for your build script:

```
buildscript {
  repositories {
    jcenter()
   }
  dependencies {
    classpath 'com.novoda:build-properties-plugin:1.0.1'
  }
}
```
Then apply the plugin in your buildscript **after the android plugin** via:  
```
apply plugin: 'com.novoda.build-properties'
```
Add a `buildProperties` configuration to your android buildscript listing
all the properties files you intend to reference in your `android` configuration:
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
where `secrets.properties` is a properties file that can now be referenced
in the buildscript as `buildProperties.secrets`.   

## Features

#### 1. Store a property value into your `BuildConfig`
In any product flavor configuration (or `defaultConfig`) you can use
`buildConfigProperty` as follows:
```
    defaultConfig {
        ...
        buildConfigProperty 'API_KEY', buildProperties.secrets['apiKey']
        ...
    }
```

#### 2. Store a property value as generated string resource
In any product flavor configuration (or `defaultConfig`) you can use
`resValueProperty` as follows:

```
    defaultConfig {
        ...
        resValueProperty 'api_key', buildProperties.secrets['apiKey']
        ...
    }
```

#### 3. Load signing configuration from properties
Instead of inline your passwords and other details in your build script
you can fill the signing configuration using a properties file.
```
signingConfigs {
  release {
    signingConfigProperties buildProperties.releaseSigning
  }
}
```
The plugin will automatically retrieve all the needed fields from the
properties file. Note: the path of the keystore file is considered relative
to the path of the specified properties file.

## Bonus

#### Typed `buildConfigField` / `resValue`
The plugin enhances the `buildConfigField` and `resValue` facilities to
enforce types. To generate a string field in your `BuildConfig` you used to write:
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
|`buildConfigString` | `buildConfigString 'TEST_STRING', 'whateva'`|
|`resValueInt`| `resValueInt 'debug_test_int', 100`|
|`resValueBoolean` | `resValueBoolean 'debug_test_bool', true`|
|`resValueString` | `resValueString 'debug_test_string', 'dunno bro...'`|

#### Fallback support
If a property cannot be found an exception is thrown. It's possible to provide a fallback
value for a given `Entry` via the `or()` operator, defined as:

| | Example |
|----|----|
|another `Entry` | `buildProperties.secrets['notThere'].or(buildProperties.secrets['fallback'])` |
|a `Closure` | `buildProperties.secrets['notThere'].or({ Math.random() })` |
|a value | `buildProperties.secrets['notThere'].or('fallback')` |

If the whole fallback chain evaluation fails a `CompositeException` is thrown listing all
the causes in the chain, eg:

```java
A problem occurred while evaluating entry:
- exception message 1
- exception message 2
- exception message 3

```

#### Override properties at build time
A property from any file listed in `buildProperties` can be overridden at
build time specifying a new value as project property (ie: `-PapiKey=newValue`).

#### Properties inheritance
It might be useful to have properties files that can recursively include
another properties files (specified via an `include` property).
Inherited properties can be overridden by the including set, just redefine
the property in the file and its value will be used instead of the one
from the included set.
 

#### More on loading properties
If the specified file is not found an exception is thrown at build time.
You can specify a custom error message to provide the user with more information.
Given a `BuildProperties` instance one of its entries can be retrieved using the `getAt` operator:

`Entry entry = buildProperties.secrets['aProperty']`

The value of an entry can be retrieved via one of the following typed accessors:

- `entry.getBoolean()`, or `entry.boolean`
- `entry.getInt()`, or `entry.int`
- `entry.getDouble()`, or `entry.double`
- `entry.getString()`, or `entry.string`

If you want to access the raw value (`Object`) you can also use `entry.getValue()`/`entry.value`.
Is important to note that values are lazily accessed too (via the internal closure provided in `Entry`).
Trying to access the value of a specific property could generate an exception
if the key is missing in the provided properties file, eg:
```
FAILURE: Build failed with an exception.

* What went wrong:
A problem occurred configuring project ':app'.
> No value defined for property 'notThere' in 'secrets' properties (/Users/toto/novoda/spikes/BuildPropertiesPlugin/sample/properties/secrets.properties)

```
