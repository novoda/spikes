package com.novoda.staticanalysis.findbugs

import com.novoda.staticanalysis.StaticAnalysisExtension
import com.novoda.staticanalysis.Violations
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.plugins.quality.FindBugs
import org.gradle.api.plugins.quality.FindBugsExtension
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.JavaExec
import org.gradle.internal.logging.ConsoleRenderer

class FindbugsConfigurator {

    void configure(Project project, Violations violations, StaticAnalysisExtension extension, Task evaluateViolations) {
        extension.ext.findbugs = { Closure config ->
            project.apply plugin: QuietFindbugsPlugin
            List<String> excludes = []
            configureExtension(project.extensions.findByType(FindBugsExtension), excludes, config)
            project.afterEvaluate {
                configureAndroidIfNeeded(project)
                project.tasks.withType(FindBugs) { FindBugs findBugs ->
                    configureTask(project, findBugs, evaluateViolations, violations, excludes)
                }
            }
        }
    }

    private void configureExtension(FindBugsExtension extension, List<String> excludes, Closure config) {
        extension.with {
            toolVersion = '3.0.1'
            ext.exclude = { String filter -> excludes.add(filter) }
            config.delegate = it
            config()
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

    private void configureAndroid(Project project, Object variants) {
        variants.all { variant ->
            FindBugs task = project.tasks.create("findbugs${variant.name.capitalize()}", QuietFindbugsPlugin.Task)
            task.with {
                group = "verification"
                description = "Run FindBugs analysis for ${variant.name} classes"
                source = variant.sourceSets.java.srcDirs.collect { it.path }.flatten()
                classes = project.fileTree(variant.javaCompile.destinationDir)
                classpath = variant.javaCompile.classpath
                dependsOn variant.javaCompile
            }
        }
    }

    private void configureTask(Project project, FindBugs findBugs, Task evaluateViolations, Violations violations, List<String> excludes) {
        findBugs.effort = 'max'
        findBugs.reportLevel = 'low'
        findBugs.ignoreFailures = true
        findBugs.reports.xml.enabled = true
        findBugs.reports.html.enabled = false
        findBugs.exclude(excludes)
        File xmlReportFile = findBugs.reports.xml.destination
        File htmlReportFile = new File(xmlReportFile.absolutePath - '.xml' + '.html')
        findBugs.doLast {
            evaluateReports(xmlReportFile, htmlReportFile, violations)
        }
        createHtmlReportTask(project, findBugs, xmlReportFile, htmlReportFile, evaluateViolations)
    }

    private GenerateHtmlReport createHtmlReportTask(Project project, FindBugs findBugs, File xmlReportFile, File htmlReportFile, evaluateViolations) {
        project.tasks.create("generate${findBugs.name.capitalize()}HtmlReport", GenerateHtmlReport) { GenerateHtmlReport generateHtmlReport ->
            generateHtmlReport.xmlReportFile = xmlReportFile
            generateHtmlReport.htmlReportFile = htmlReportFile
            generateHtmlReport.classpath = findBugs.findbugsClasspath
            generateHtmlReport.dependsOn findBugs
            evaluateViolations.dependsOn generateHtmlReport
        }
    }

    private void evaluateReports(File xmlReportFile, File htmlReportFile, Violations violations) {
        def evaluator = new FinbugsViolationsEvaluator(xmlReportFile)
        String reportUrl = new ConsoleRenderer().asClickableFileUrl(htmlReportFile)
        violations.addViolations(evaluator.errorsCount(), evaluator.warningsCount(), reportUrl)
    }

    static class GenerateHtmlReport extends JavaExec {
        @Input
        File xmlReportFile

        @Input
        File htmlReportFile

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
            new FileOutputStream(htmlReportFile)
        }
    }

}
