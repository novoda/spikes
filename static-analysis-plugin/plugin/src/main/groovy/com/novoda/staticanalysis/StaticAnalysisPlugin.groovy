package com.novoda.staticanalysis

import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.quality.Checkstyle

class StaticAnalysisPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        StaticAnalysisExtension extension = project.extensions.create('staticAnalysis', StaticAnalysisExtension)
        configureCheckstyle(project, extension)
    }

    private void configureCheckstyle(Project project, StaticAnalysisExtension extension) {
        project.apply plugin: 'checkstyle'
        project.afterEvaluate {
            project.tasks.withType(Checkstyle) { task ->
                task.with {
                    group = 'verification'
                    showViolations = false
                    switch (extension.penalty) {
                        case Penalty.NONE:
                            ignoreFailures = true
                            break
                        case Penalty.ERRORS:
                            ignoreFailures = false
                            break
                        case Penalty.WARNINGS:
                            ignoreFailures = false
                            doLast {
                                File xmlReportFile = reports.xml.destination
                                File htmlReportFile = new File(xmlReportFile.absolutePath - '.xml' + '.html')
                                if (xmlReportFile.exists() && xmlReportFile.text.contains("<error ")) {
                                    throw new GradleException("Checkstyle rule violations were found. See the report at: ${htmlReportFile ?: xmlReportFile}")
                                }
                            }
                            break
                    }
                }
            }
        }
    }

}
