package com.novoda.test

import com.novoda.staticanalysis.Penalty;

class BuildScriptBuilder {
    private static final Closure<String> JAVA_TEMPLATE = { BuildScriptBuilder builder ->
        """
plugins {
    id 'static-analysis'
}
repositories {
    jcenter()
}
apply plugin: 'java'
sourceSets {
    main {
        ${builder.formatSrcDirs()}
    }
}
staticAnalysis {
    ${builder.formatPenalty()}
}
"""
    }

    private static final Closure<String> ANDROID_TEMPLATE = { BuildScriptBuilder builder ->
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

    private final Closure<String> template
    private final List<File> srcDirs = new ArrayList<>()
    private Penalty penalty

    public static BuildScriptBuilder forJava() {
        new BuildScriptBuilder(JAVA_TEMPLATE)
    }

    public static BuildScriptBuilder forAndroid() {
        new BuildScriptBuilder(ANDROID_TEMPLATE)
    }

    private BuildScriptBuilder(Closure<String> template) {
        this.template = template
    }

    BuildScriptBuilder withSrcDirs(File... srcDirs) {
        this.srcDirs.addAll(srcDirs)
        return this
    }

    BuildScriptBuilder withPenalty(Penalty penalty) {
        this.penalty = penalty
        return this
    }

    String build() {
        template.call(this)
    }

    private String formatSrcDirs() {
        srcDirs.isEmpty() ? '' : """java {
            ${srcDirs.collect { "srcDir '$it'" }.join("\n\t\t\t\t\t")}
        }"""
    }

    private String formatPenalty() {
        switch (penalty) {
            case Penalty.NONE:
                return 'penalty = none'
            case Penalty.FAIL_ON_ERRORS:
                return 'penalty = failOnErrors'
            case Penalty.FAIL_ON_WARNINGS:
                return 'penalty = failOnWarnings'
            default:
                return ''
        }
    }

}
