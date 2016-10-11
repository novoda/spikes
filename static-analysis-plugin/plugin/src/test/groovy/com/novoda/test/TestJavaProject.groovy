package com.novoda.test

final class TestJavaProject extends TestProject {

    private static final Closure<String> TEMPLATE = { TestProject project ->
        """
plugins {
    id 'static-analysis'
}
repositories {
    jcenter()
}
apply plugin: 'java'
sourceSets {
    ${formatSourceSets(project)}
}
${formatExtension(project)}
"""
    }

    TestJavaProject() {
        super(TEMPLATE)
    }

    private static String formatSourceSets(TestProject project) {
        project.sourceSets
                .entrySet()
                .collect { Map.Entry<String, List<String>> entry ->
            """$entry.key {
        java {
            ${entry.value.collect { "srcDir '$it'" }.join('\n\t\t\t\t')}
        }
    }"""
        }
        .join('\n\t')
    }
}
