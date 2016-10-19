package com.novoda.staticanalysis

import org.gradle.api.*

class StaticAnalysisPlugin implements Plugin<Project> {
    private final CheckstyleConfigurator checkstyleConfigurator = new CheckstyleConfigurator()

    @Override
    void apply(Project project) {
        StaticAnalysisExtension extension = project.extensions.create('staticAnalysis', StaticAnalysisExtension)

        NamedDomainObjectContainer<Violations> allViolations = project.container(Violations)
        Task evaluateViolations = project.tasks.create('evaluateViolations', EvaluateViolationsTask) { task ->
            task.penalty = extension.penalty
            task.allViolations = allViolations
        }
        checkstyleConfigurator.configure(project, allViolations.create('Checkstyle'), evaluateViolations)
        project.afterEvaluate {
            project.tasks['check'].dependsOn evaluateViolations
        }
    }

}
