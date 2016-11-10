package com.novoda.gradle.diffdependencytask

import com.novoda.gradle.diffdependencytask.ext.ExtensionRegister
import org.gradle.api.Plugin
import org.gradle.api.Project

class DiffDependencyTaskPlugin implements Plugin<Project> {

    private static String TASK_CONFIG_PROPERTY_NAME = 'diffDependency'

    @Override
    void apply(Project project) {

        ExtensionRegister.INSTANCE.run()

        project.extensions.add(TASK_CONFIG_PROPERTY_NAME, new DiffDependencyTaskConfig())

        project.extensions.add('registerConditionalDependency', { ConditionalDependency conditionalDependency ->
            def config = project.extensions.getByName(TASK_CONFIG_PROPERTY_NAME) as DiffDependencyTaskConfig
            config.conditionalDependencyRepository.add(conditionalDependency)
            project
        })

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
