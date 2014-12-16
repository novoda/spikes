package com.novoda.comparereports

import org.gradle.api.Plugin
import org.gradle.api.Project

class ComparePlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.extensions.create('compareReports', ReportsExtension.class)
        // TODO
    }

}
