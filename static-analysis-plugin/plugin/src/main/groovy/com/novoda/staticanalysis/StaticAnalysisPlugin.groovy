package com.novoda.staticanalysis

import org.gradle.api.Plugin
import org.gradle.api.Project

class StaticAnalysisPlugin implements Plugin<Project> {
    private final CheckstyleConfigurator checkstyleConfigurator = new CheckstyleConfigurator()

    @Override
    void apply(Project project) {
        StaticAnalysisExtension extension = project.extensions.create('staticAnalysis', StaticAnalysisExtension)
        checkstyleConfigurator.configure(project, extension)
    }

}
