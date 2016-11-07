package com.novoda.staticanalysis.findbugs

import com.novoda.staticanalysis.StaticAnalysisExtension
import com.novoda.staticanalysis.Violations
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.plugins.quality.FindBugs
import org.gradle.api.plugins.quality.FindBugsExtension

class FindbugsConfigurator {

    void configure(Project project, Violations violations, StaticAnalysisExtension extension, Task evaluateViolations) {
        extension.ext.findbugs = { Closure config ->
            project.apply plugin: QuietFindbugsPlugin
            List<String> excludes = []
            configureExtension(project.extensions.findByType(FindBugsExtension), excludes, config)
            project.afterEvaluate {
                project.tasks.withType(FindBugs) { FindBugs findBugs ->
                    configureTask(project, findBugs, evaluateViolations, violations)
                }
            }
        }
    }

    private void configureTask(Project project, FindBugs findBugs, Task evaluateViolations, Violations violations) {
        findBugs.effort = 'max'
        findBugs.reportLevel = 'low'
        findBugs.ignoreFailures = true
        findBugs.reports.xml.enabled = true
        findBugs.reports.html.enabled = false
        evaluateViolations.dependsOn findBugs
    }

    private void configureExtension(FindBugsExtension extension, List<String> excludes, Closure config) {
        extension.with {
            toolVersion = '3.0.1'
            config.delegate = it
            config()
        }
    }
}
