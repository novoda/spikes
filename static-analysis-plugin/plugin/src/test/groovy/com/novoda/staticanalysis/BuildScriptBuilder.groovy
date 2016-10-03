package com.novoda.staticanalysis;

class BuildScriptBuilder {

    private final List<File> srcDirs = new ArrayList<>()
    private Penalty penalty
    private Closure<String> extensionTemplate = {
        """
staticAnalysis {
    ${penalty ? "penalty = ${Penalty.class.canonicalName}.${penalty.name()}" :''}
}
"""
    }

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
${extensionTemplate.call()}
"""
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
        javaTemplate.call()
    }

}
