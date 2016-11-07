package com.novoda.staticanalysis.findbugs

import com.novoda.staticanalysis.StaticAnalysisExtension
import com.novoda.staticanalysis.Violations
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.plugins.quality.FindBugs
import org.gradle.api.plugins.quality.FindBugsExtension
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.JavaExec

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

        project.tasks.create("${findBugs.name}GenerateHtmlReport", GenerateHtmlReport) { GenerateHtmlReport generateHtmlReport ->
            generateHtmlReport.xmlReportFile = findBugs.reports.xml.destination
            generateHtmlReport.classpath = findBugs.findbugsClasspath
            generateHtmlReport.dependsOn findBugs
            evaluateViolations.dependsOn generateHtmlReport
        }
    }

    private void configureExtension(FindBugsExtension extension, List<String> excludes, Closure config) {
        extension.with {
            toolVersion = '3.0.1'
            config.delegate = it
            config()
        }
    }

    static class GenerateHtmlReport extends JavaExec {
        @Input
        File xmlReportFile

        @Override
        void exec() {
            if (xmlReportFile != null && xmlReportFile.exists()) {
                main = 'edu.umd.cs.findbugs.PrintingBugReporter'
                standardOutput = createHtmlReportOutput()
                args '-html', xmlReportFile
                super.exec()
            }
        }

        private FileOutputStream createHtmlReportOutput() {
            new FileOutputStream(new File(xmlReportFile.absolutePath - '.xml' + '.html'))
        }
    }

}
