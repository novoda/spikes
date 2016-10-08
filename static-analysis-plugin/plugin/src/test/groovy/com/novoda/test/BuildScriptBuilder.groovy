package com.novoda.test

class BuildScriptBuilder {
    private final Closure<String> PENALTY_TEMPLATE = {
        """
staticAnalysis {
    penalty ${penalty}
}
"""
    }
    private final Closure<String> fullTemplate
    private final List<File> srcDirs = new ArrayList<>()
    private String penalty

    BuildScriptBuilder(Closure<String> fullTemplate) {
        this.fullTemplate = fullTemplate
    }

    BuildScriptBuilder withSrcDirs(File... srcDirs) {
        this.srcDirs.addAll(srcDirs)
        return this
    }

    BuildScriptBuilder withPenalty(String penalty) {
        this.penalty = penalty
        return this
    }

    String formatSrcDirs() {
        srcDirs.isEmpty() ? '' : """java {
            ${srcDirs.collect { "srcDir '$it'" }.join("\n\t\t\t")}
        }"""
    }

    String formatExtension() {
        PENALTY_TEMPLATE.call()
    }

    String build() {
        fullTemplate.call(this)
    }
}
