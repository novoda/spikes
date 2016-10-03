package com.novoda.staticanalysis

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.quality.Checkstyle

class StaticAnalysisPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.extensions.create('staticAnalysis', StaticAnalysisExtension)
        configureCheckstyle(project)
    }

    private void configureCheckstyle(Project project) {
        project.apply plugin: 'checkstyle'
        project.afterEvaluate {
            project.tasks.withType(Checkstyle) { task ->
                task.ignoreFailures = false
                task.showViolations = false
            }
        }
    }

}
