package com.novoda.staticanalysis

import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task

class StaticAnalysisPlugin implements Plugin<Project> {
    private final CheckstyleConfigurator checkstyleConfigurator = new CheckstyleConfigurator()
    private final PmdConfigurator pmdConfigurator = new PmdConfigurator()

    @Override
    void apply(Project project) {
        StaticAnalysisExtension extension = project.extensions.create('staticAnalysis', StaticAnalysisExtension)

        NamedDomainObjectContainer<Violations> allViolations = project.container(Violations)
        Task evaluateViolations = project.tasks.create('evaluateViolations', EvaluateViolationsTask) { task ->
            task.penalty = extension.penalty
            task.allViolations = allViolations
        }
        checkstyleConfigurator.configure(project, allViolations.create('Checkstyle'), extension, evaluateViolations)
        pmdConfigurator.configure(project, allViolations.create('PMD'), extension, evaluateViolations)
        project.afterEvaluate {
            project.tasks['check'].dependsOn evaluateViolations
        }
    }

}
