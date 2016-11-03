package com.novoda.staticanalysis

import com.novoda.staticanalysis.PmdViolationsEvaluator.PmdViolation
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.plugins.quality.Pmd
import org.gradle.api.plugins.quality.PmdExtension
import org.gradle.internal.logging.ConsoleRenderer

class PmdConfigurator {

    void configure(Project project, Violations violations, StaticAnalysisExtension extension, Task evaluateViolations) {
        extension.ext.pmd = { Closure config ->
            project.apply plugin: 'pmd'
            List<String> excludes = []
            project.extensions.findByType(PmdExtension).with {
                toolVersion = '5.5.1'
                ext.exclude = { String filter ->
                    excludes.add(filter)
                }
                config.delegate = it
                config()
            }
            project.afterEvaluate {
                project.tasks.withType(Pmd) { pmd ->
                    pmd.group = 'verification'
                    pmd.ignoreFailures = true
                    pmd.rulePriority = 5
                    pmd.metaClass.getLogger = { QuietLogger.INSTANCE }
                    pmd.exclude(excludes)
                    pmd.doLast {
                        File xmlReportFile = pmd.reports.xml.destination
                        File htmlReportFile = new File(xmlReportFile.absolutePath - '.xml' + '.html')
                        evaluateReports(xmlReportFile, htmlReportFile, violations)
                    }
                    evaluateViolations.dependsOn pmd
                }
            }
        }
    }

    private void evaluateReports(File xmlReportFile, File htmlReportFile, Violations violations) {
        PmdViolationsEvaluator evaluator = new PmdViolationsEvaluator(xmlReportFile)
        int errors = 0, warnings = 0
        evaluator.collectViolations().each { PmdViolation violation ->
            if (violation.isError()) {
                errors += 1
            } else {
                warnings += 1
            }
        }
        String reportUrl = new ConsoleRenderer().asClickableFileUrl(htmlReportFile ?: xmlReportFile)
        violations.addViolations(errors, warnings, reportUrl)
    }

}
