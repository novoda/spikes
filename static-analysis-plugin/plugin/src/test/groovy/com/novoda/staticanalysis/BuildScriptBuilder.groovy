package com.novoda.staticanalysis;

class BuildScriptBuilder {

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
            ${format(srcDirs)}
        }
    }
}
staticAnalysis {
    ${format(penalty)}
}
"""
    }

    private final List<File> srcDirs = new ArrayList<>()
    private Penalty penalty

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

    private static String format(List<File> srcDirs) {
        srcDirs.collect { "srcDir '$it'" }.join("\n\t\t\t\t\t")
    }

    private static String format(Penalty penalty) {
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
