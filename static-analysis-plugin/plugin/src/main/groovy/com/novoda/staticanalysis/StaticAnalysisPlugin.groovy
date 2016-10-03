package com.novoda.staticanalysis

import org.gradle.api.Plugin
import org.gradle.api.Project

class StaticAnalysisPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        StaticAnalysisExtension extension = project.extensions.create('staticAnalysis', StaticAnalysisExtension)
        new CheckstyleConfigurator().configure(project, extension)
    }

}
