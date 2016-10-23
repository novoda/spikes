package com.novoda.staticanalysis

import groovy.util.slurpersupport.GPathResult
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.plugins.quality.Checkstyle
import org.gradle.api.plugins.quality.CheckstyleExtension
import org.gradle.internal.logging.ConsoleRenderer

class CheckstyleConfigurator {

    void configure(Project project, Violations violations, Task evaluateViolations) {
        List<String> excludes = []
        project.apply plugin: 'checkstyle'
        project.checkstyle {
            toolVersion = '7.1.2'
            ext.exclude = { String filter ->
                excludes.addAll(filter)
            }
        }
        project.afterEvaluate {
            boolean isAndroidApp = project.plugins.hasPlugin('com.android.application')
            boolean isAndroidLib = project.plugins.hasPlugin('com.android.library')
            if (isAndroidApp || isAndroidLib) {
                def variants = isAndroidApp ? project.android.applicationVariants : project.android.libraryVariants
                configureAndroid(project, variants)
            }
            project.tasks.withType(Checkstyle) { checkstyle ->
                checkstyle.group = 'verification'
                checkstyle.showViolations = false
                checkstyle.ignoreFailures = true
                checkstyle.metaClass.getLogger = { QuietLogger.INSTANCE }
                checkstyle.exclude(excludes)
                checkstyle.doLast {
                    File xmlReportFile = checkstyle.reports.xml.destination
                    File htmlReportFile = new File(xmlReportFile.absolutePath - '.xml' + '.html')

                    GPathResult xml = new XmlSlurper().parse(xmlReportFile)
                    int errors = xml.'**'.findAll { node -> node.name() == 'error' && node.@severity == 'error' }.size()
                    int warnings = xml.'**'.findAll { node -> node.name() == 'error' && node.@severity == 'warning' }.size()
                    String reportUrl = new ConsoleRenderer().asClickableFileUrl(htmlReportFile ?: xmlReportFile)
                    violations.addViolations(errors, warnings, reportUrl)
                }
                evaluateViolations.dependsOn checkstyle
            }
        }
    }

    private static void configureAndroid(Project project, Object variants) {
        project.with {
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
            }
        }
    }
}
