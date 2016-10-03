package com.novoda.staticanalysis;

class BuildScriptBuilder {

    private final List<File> srcDirs = new ArrayList<>()

    private final Closure<String> javaTemplate = {
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
        java {
            ${srcDirs.collect { "srcDir '$it'" }.join("\n\t\t\t\t\t")}
        }
    }
}
"""
    }

    BuildScriptBuilder withSrcDir(File srcDir) {
        srcDirs.add(srcDir)
        return this
    }

    String build() {
        javaTemplate.call()
    }

}
