package com.novoda.test

final class TestJavaProject extends TestProject {

    private static final Closure<String> TEMPLATE = { BuildScriptBuilder builder ->
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

    TestJavaProject() {
        super(new BuildScriptBuilder(TEMPLATE))
    }
}
