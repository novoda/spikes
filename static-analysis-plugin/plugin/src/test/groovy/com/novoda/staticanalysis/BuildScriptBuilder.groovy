package com.novoda.staticanalysis;

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

    private final Closure<String> template
    private final List<File> srcDirs = new ArrayList<>()
    private Penalty penalty

    public static BuildScriptBuilder forJava() {
        new BuildScriptBuilder(JAVA_TEMPLATE)
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
