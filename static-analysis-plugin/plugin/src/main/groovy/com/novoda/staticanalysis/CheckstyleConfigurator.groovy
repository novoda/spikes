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
            boolean isAndroidApp = project.plugins.hasPlugin('com.android.application')
            boolean isAndroidLib = project.plugins.hasPlugin('com.android.library')
            if (isAndroidApp || isAndroidLib) {
                def variants = isAndroidApp ? project.android.applicationVariants : project.android.libraryVariants
                configureAndroid(project, variants)
            }
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

    private static void configureAndroid(Project project, Object variants) {
        project.with {
            def check = tasks['check']
            android.sourceSets.all { sourceSet ->
                def sourceDirs = sourceSet.java.srcDirs
                def notEmptyDirs = sourceDirs.findAll { it.list()?.length > 0 }
                if (notEmptyDirs.empty) {
                    return
                }
                Checkstyle checkstyle = tasks.create("checkstyle${sourceSet.name.capitalize()}", Checkstyle)
                checkstyle.with {
                    description = "Run Checkstyle analysis for ${sourceSet.name} classes"
                    source = sourceSet.java.srcDirs
                    classpath = files("$buildDir/intermediates/classes/")
                }
                variants.all { variant ->
                    checkstyle.mustRunAfter variant.javaCompile
                }
                check.dependsOn checkstyle
            }
        }
    }


}
