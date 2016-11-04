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
            configureExtension(project.extensions.findByType(PmdExtension), excludes, config)
            project.afterEvaluate {
                configureAndroidIfNeeded(project)
                project.tasks.withType(Pmd) { pmd ->
                    configureTask(pmd, violations, excludes)
                    evaluateViolations.dependsOn pmd
                }
            }
        }
    }

    private void configureAndroidIfNeeded(Project project) {
        boolean isAndroidApp = project.plugins.hasPlugin('com.android.application')
        boolean isAndroidLib = project.plugins.hasPlugin('com.android.library')
        if (isAndroidApp || isAndroidLib) {
            def variants = isAndroidApp ? project.android.applicationVariants : project.android.libraryVariants
            configureAndroid(project, variants)
        }
    }

    private Object configureExtension(PmdExtension extension, List<String> excludes, Closure config) {
        extension.with {
            toolVersion = '5.5.1'
            ext.exclude = { String filter ->
                excludes.add(filter)
            }
            config.delegate = it
            config()
        }
    }

    private void configureAndroid(Project project, Object variants) {
        project.with {
            android.sourceSets.all { sourceSet ->
                def sourceDirs = sourceSet.java.srcDirs
                def notEmptyDirs = sourceDirs.findAll { it.list()?.length > 0 }
                if (notEmptyDirs.empty) {
                    return
                }
                Pmd pmd = tasks.create("pmd${sourceSet.name.capitalize()}", Pmd)
                pmd.with {
                    description = "Run PMD analysis for ${sourceSet.name} classes"
                    source = sourceSet.java.srcDirs
                }
                variants.all { variant ->
                    pmd.mustRunAfter variant.javaCompile
                }
            }
        }
    }

    private void configureTask(Pmd pmd, Violations violations, List<String> excludes) {
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
