gradle-buildproperties-plugin
=============================

## How to use

Apply the plugin to your project's `build.gradle`:

```groovy
apply plugin: 'com.novoda.buildproperties'
```

Then, configure the plugin by specifying the file it needs to read data from:

```groovy
buildProperties {
    file = 'my-props/config.properties'
}
```

As soon as the plugin is applied to the project, you can access `project.buildProperties` and use
the available getters (see [BuildProperties.groovy](buildSrc/src/main/groovy/com/novoda/buildproperties/BuildProperties.groovy))
to read the data you need:

```groovy
version project.buildProperties.getString('version')
```

## Properties files

The properties files are key-value dictionaries, with optional inclusions of other files.

The following example shows how to include multiple files in another one:

```properties
include=basic-prop.properties
include=extra-prop.properties
version=0.0.2
description=This is a string
```

Let'assume that `basic-prop.properties` contains:

```properties
version=0.0.1
text=banana
```

If `extra-prop.properties` contains:

```properties
random=129342323
```

Then the end result will be the same as the following:

```properties
text=banana
random=129342323
version=0.0.2
description=This is a string
```

Properties from included files will, therefore, be replaced if specified in an "upper hierarchy" file.
