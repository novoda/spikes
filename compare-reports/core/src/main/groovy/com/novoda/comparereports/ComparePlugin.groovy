package com.novoda.comparereports

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task

class ComparePlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        ReportsExtension reportsExtension = project.extensions.create('compareReports', ReportsExtension.class)

        Task compareReportsTask = project.task(type: CompareReportsTask.class, dependsOn: 'check', 'compareReports')
        project.afterEvaluate {
            compareReportsTask.with {
                extension = reportsExtension
            }
        }
    }

}
