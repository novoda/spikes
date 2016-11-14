package com.novoda.gradle.diffdependencytask

import org.gradle.api.Plugin
import org.gradle.api.Project

import java.util.regex.Pattern

class DiffDependencyTaskPlugin implements Plugin<Project> {

    private static String TASK_CONFIG_PROPERTY_NAME = 'diffDependency'

    private final config = new DiffDependencyTaskConfig()

    @Override
    void apply(Project project) {

        project.extensions.add(TASK_CONFIG_PROPERTY_NAME, config)

        project.tasks.all { task ->
            task.ext.onDiffDependsOn = { def patterns, Object... tasks ->
                if (patterns instanceof Pattern) {
                     patterns = [patterns]
                }
                def conditionalDependency = new ConditionalDependency(task, patterns, tasks)
                config.conditionalDependencyRepository.add(conditionalDependency)
                task
            }
        }

        project.afterEvaluate {
            def evaluator = new ConditionalDependencyEvaluator(
                    config.changedFilesProvider,
                    config.conditionalDependencyRepository
            )

            evaluator()
        }

    }

}
