package com.novoda.buildproperties

import org.gradle.api.NamedDomainObjectFactory
import org.gradle.api.Plugin
import org.gradle.api.Project

class BuildPropertiesPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        def container = project.container(BuildProperties, new NamedDomainObjectFactory<BuildProperties>() {
            @Override
            BuildProperties create(String name) {
                return new BuildProperties(name, project)
            }
        })
        container.create('env').entries(new EnvironmentPropertiesEntries(project))
        project.extensions.add('buildProperties', container)

        project.plugins.withId('com.android.application') {
            amendAndroidExtension(project)
        }
        project.plugins.withId('com.android.library') {
            amendAndroidExtension(project)
        }
    }

    private static void amendAndroidExtension(Project project) {
        def android = project.extensions.findByName("android")
        android.defaultConfig.with {
            addBuildConfigSupportTo(it, project)
            addResValueSupportTo(it, project)
        }

        android.productFlavors.all {
            addBuildConfigSupportTo(it, project)
            addResValueSupportTo(it, project)
        }

        android.buildTypes.all {
            addBuildConfigSupportTo(it, project)
            addResValueSupportTo(it, project)
        }

        android.signingConfigs.all {
            addSigningConfigSupportTo(it)
        }
    }

    private static void addBuildConfigSupportTo(target, Project project) {
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
        target.ext.buildConfigProperty = { String name, Entry entry ->
            project.afterEvaluate {
                target.buildConfigField 'String', name, "\"${entry.string}\""
            }
        }
    }

    private static void addResValueSupportTo(target, Project project) {
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
        target.ext.resValueProperty = { String name, Entry entry ->
            project.afterEvaluate {
                target.resValue 'string', name, "\"${entry.string}\""
            }
        }
    }

    private static void addSigningConfigSupportTo(target) {
        target.ext.signingConfigProperties = { BuildProperties buildProperties ->
            target.storeFile new File(buildProperties.parentFile, buildProperties['storeFile'].string)
            target.storePassword buildProperties['storePassword'].string
            target.keyAlias buildProperties['keyAlias'].string
            target.keyPassword buildProperties['keyPassword'].string
        }
    }

}
