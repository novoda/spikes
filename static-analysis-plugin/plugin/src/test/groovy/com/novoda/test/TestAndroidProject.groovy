package com.novoda.test

class TestAndroidProject extends TestProject {
    private static final Closure<String> TEMPLATE = { BuildScriptBuilder builder ->
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
        main {
            manifest.srcFile '${Fixtures.ANDROID_MANIFEST}'
            ${builder.formatSrcDirs()}
        }
    }
}
staticAnalysis {
    ${builder.formatPenalty()}
}
"""
    }

    TestAndroidProject() {
        super(new BuildScriptBuilder(TEMPLATE))
        copyFile(Fixtures.LOCAL_PROPERTIES, 'local.properties')
    }

    @Override
    List<String> defaultArguments() {
        ['-x', 'lint'] + super.defaultArguments()
    }
}
