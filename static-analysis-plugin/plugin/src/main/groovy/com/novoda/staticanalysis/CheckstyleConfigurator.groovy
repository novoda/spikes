package com.novoda.staticanalysis

import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.plugins.quality.Checkstyle
import org.gradle.internal.logging.ConsoleRenderer;

class CheckstyleConfigurator {

    void configure(Project project, StaticAnalysisExtension extension) {
        project.apply plugin: 'checkstyle'
        project.afterEvaluate {
            Penalty penalty = extension.penalty
            project.tasks.withType(Checkstyle) { task ->
                task.group = 'verification'
                task.showViolations = false
                task.ignoreFailures = (penalty == Penalty.NONE)
                if (penalty == Penalty.FAIL_ON_WARNINGS) {
                    task.doLast {
                        File xmlReportFile = task.reports.xml.destination
                        File htmlReportFile = new File(xmlReportFile.absolutePath - '.xml' + '.html')
                        if (xmlReportFile.exists() && xmlReportFile.text.contains("<error ")) {
                            String reportUrl = new ConsoleRenderer().asClickableFileUrl(htmlReportFile ?: xmlReportFile)
                            String message = "Checkstyle rule violations were found. See the report at: ${reportUrl}"
                            throw new GradleException(message)
                        }
                    }
                }
            }
        }
    }

}
