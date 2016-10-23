package com.novoda.test

class TestAndroidProject extends TestProject {
    private static final Closure<String> TEMPLATE = { TestProject project ->
        """
buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        classpath 'com.android.tools.build:gradle:2.2.0'
    }
}
plugins {
    id 'static-analysis'
}
repositories {
    jcenter()
}
apply plugin: 'com.android.library'
android {
    compileSdkVersion 24
    buildToolsVersion "24.0.2"

    defaultConfig {
        minSdkVersion 16
        targetSdkVersion 23
        versionCode 1
        versionName "1.0"
    }
    sourceSets {
        ${formatSourceSets(project)}
    }
}
${formatExtension(project)}
"""
    }

    TestAndroidProject() {
        super(TEMPLATE)
        withFile(Fixtures.LOCAL_PROPERTIES, 'local.properties')
    }

    private static String formatSourceSets(TestProject project) {
        project.sourceSets
                .entrySet()
                .collect { Map.Entry<String, List<String>> entry ->
            """$entry.key {
            manifest.srcFile '${Fixtures.ANDROID_MANIFEST}'
            java {
                ${entry.value.collect { "srcDir '$it'" }.join('\n\t\t\t\t')}
            }
        }"""
        }
        .join('\n\t\t')
    }

    @Override
    List<String> defaultArguments() {
        ['-x', 'lint'] + super.defaultArguments()
    }
}
