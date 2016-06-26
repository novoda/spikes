package com.novoda.buildproperties

import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project

class BuildPropertiesPlugin implements Plugin<Project> {

    private final Map<String, BuildProperties> propertiesMap = [:]

    @Override
    void apply(Project project) {
        boolean isAndroidApp = project.plugins.hasPlugin('com.android.application')
        if (!isAndroidApp) {
            throw new GradleException("This plugin can be applied only to an Android application project")
        }

        project.android.extensions.add('buildProperties', project.container(BuildProperties))
        def container = project.android.extensions.getByName('buildProperties')
        project.afterEvaluate {
            container.all { BuildProperties buildProperties ->
                Properties properties = new Properties()
                properties.load(new FileInputStream(buildProperties.file))
                buildProperties.setEntries(properties)
                propertiesMap[buildProperties.name] = buildProperties
            }
        }

        def android = project.extensions.findByName("android")

        android.defaultConfig.with {
            addPropertiesLoadSupport(it)
            addBuildConfigSupportTo(it, project)
            addResValueSupportTo(it, project)
        }

        android.productFlavors.all {
            addPropertiesLoadSupport(it)
            addBuildConfigSupportTo(it, project)
            addResValueSupportTo(it, project)
        }

        android.buildTypes.all {
            addPropertiesLoadSupport(it)
            addBuildConfigSupportTo(it, project)
            addResValueSupportTo(it, project)
        }

        android.signingConfigs.all {
            addPropertiesLoadSupport(it)
            addSigningConfigSupportTo(it, project)
        }
    }

    private void addPropertiesLoadSupport(target) {
        target.ext.from = { BuildProperties buildProperties ->
            return { propertiesMap[buildProperties.name] }
        }
        target.ext.load = { File file ->
            return {
                BuildProperties buildProperties = new BuildProperties(file.path)
                buildProperties.entries = load(file)
                buildProperties.file = file
                buildProperties
            }.memoize()
        }
    }

    private Properties load(File file) {
        Properties properties = new Properties()
        properties.load(new FileInputStream(file))
        properties
    }

    private void addBuildConfigSupportTo(target, Project project) {
        def buildConfigField = { String type, String name, Closure<String> value ->
            target.buildConfigField type, name, value()
        }

        target.ext.buildConfigBoolean = { String name, Boolean value ->
            buildConfigField('boolean', name, { Boolean.toString(value) })
        }
        target.ext.buildConfigInt = { String name, Integer value ->
            buildConfigField('int', name, { Integer.toString(value) })
        }
        target.ext.buildConfigLong = { String name, Long value ->
            buildConfigField('long', name, { "${Long.toString(value)}L" })
        }
        target.ext.buildConfigDouble = { String name, Double value ->
            buildConfigField('double', name, { Double.toString(value) })
        }
        target.ext.buildConfigString = { String name, String value ->
            buildConfigField('String', name, { "\"$value\"" })
        }
        target.ext.buildConfigProperty = { String name = null, Closure<BuildProperties> getBuildProperties, String key ->
            project.afterEvaluate {
                Properties properties = getBuildProperties().entries
                target.buildConfigField 'String', name ?: formatBuildConfigField(key), "\"${properties[key]}\""
            }
        }
        target.ext.buildConfigProperties = { Closure<BuildProperties> getBuildProperties ->
            project.afterEvaluate {
                Properties properties = getBuildProperties().entries
                properties.stringPropertyNames().each { name ->
                    target.buildConfigField 'String', formatBuildConfigField(name), "\"${properties[name]}\""
                }
            }
        }
    }

    private String formatBuildConfigField(String name) {
        return name
                .replace('.', '_')
                .split("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])")
                .join('_')
                .toUpperCase()
    }

    private void addResValueSupportTo(target, Project project) {
        def resValue = { String type, String name, Closure<String> value ->
            target.resValue type, name, value()
        }
        target.ext.resValueBoolean = { String name, Boolean value ->
            resValue('bool', name, { Boolean.toString(value) })
        }
        target.ext.resValueInt = { String name, Integer value ->
            resValue('integer', name, { Integer.toString(value) })
        }
        target.ext.resValueString = { String name, String value ->
            resValue('string', name, { "\"$value\"" })
        }
        target.ext.resValueProperty = { String name = null, Closure<BuildProperties> getBuildProperties, String key ->
            project.afterEvaluate {
                Properties properties = getBuildProperties().entries
                target.resValue 'string', name ?: key, "\"${properties[key]}\""
            }
        }
        target.ext.resValueProperties = { Closure<BuildProperties> getBuildProperties ->
            project.afterEvaluate {
                Properties properties = getBuildProperties().entries
                properties.stringPropertyNames().each { name ->
                    target.resValue 'string', name, properties.getProperty(name)
                }
            }
        }
    }

    private void addSigningConfigSupportTo(target, Project project) {
        target.ext.signingConfigProperties = { Closure<BuildProperties> getBuildProperties ->
            project.afterEvaluate {
                BuildProperties buildProperties = getBuildProperties()
                Properties properties = buildProperties.entries
                target.storeFile new File(buildProperties.parentFile, properties.storeFile)
                target.storePassword properties.storePassword
                target.keyAlias properties.keyAlias
                target.keyPassword properties.keyPassword
            }
        }
    }

}
