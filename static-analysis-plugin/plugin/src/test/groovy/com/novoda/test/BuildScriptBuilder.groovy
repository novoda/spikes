package com.novoda.test

import com.novoda.staticanalysis.Penalty;

class BuildScriptBuilder {
    private final Closure<String> template
    private final List<File> srcDirs = new ArrayList<>()
    private Penalty penalty

    BuildScriptBuilder(Closure<String> template) {
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

    String formatSrcDirs() {
        srcDirs.isEmpty() ? '' : """java {
            ${srcDirs.collect { "srcDir '$it'" }.join("\n\t\t\t\t\t")}
        }"""
    }

    String formatPenalty() {
        switch (penalty) {
            case Penalty.NONE:
                return 'penalty none'
            case Penalty.FAIL_ON_ERRORS:
                return 'penalty failOnErrors'
            case Penalty.FAIL_ON_WARNINGS:
                return 'penalty failOnWarnings'
            default:
                return ''
        }
    }

    String build() {
        template.call(this)
    }
}
