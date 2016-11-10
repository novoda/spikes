package com.novoda.gradle.diffdependencytask

import org.gradle.api.Plugin
import org.gradle.api.Project

import java.util.regex.Pattern

class DiffDependencyTaskPlugin implements Plugin<Project> {

    private static String TASK_CONFIG_PROPERTY_NAME = 'diffDependency'

    @Override
    void apply(Project project) {

        project.extensions.add(TASK_CONFIG_PROPERTY_NAME, new DiffDependencyTaskConfig())

        project.extensions.add('registerConditionalDependency', { ConditionalDependency conditionalDependency ->
            def config = project.extensions.getByName(TASK_CONFIG_PROPERTY_NAME) as DiffDependencyTaskConfig
            config.conditionalDependencyRepository.add(conditionalDependency)
            project
        })

        project.tasks.all {
            it.extensions.add('ifDiffMatches', { Pattern[] patterns ->
                new ConditionalDependency(it, patterns)
            })
        }

        project.afterEvaluate {
            def config = project.extensions.getByName(TASK_CONFIG_PROPERTY_NAME) as DiffDependencyTaskConfig
            def evaluator = new ConditionalDependencyEvaluator(
                    config.changedFilesProvider,
                    config.conditionalDependencyRepository
            )

            evaluator()
        }

    }

}
