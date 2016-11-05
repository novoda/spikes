package com.novoda.gradle.diffdependencytask

import org.gradle.api.Project
import org.gradle.api.Task

import java.util.regex.Pattern

public class ConditionalDependency {

    static {
        Project.metaClass.registerConditionalDependency << { ConditionalDependency conditionalDependency ->
            def config = delegate.extensions.getByName(Constants.TASK_CONFIG_PROPERTY_NAME) as DiffDependencyTaskConfig
            config.conditionalDependencyRepository.add(conditionalDependency)
            delegate
        }
    }

    final Pattern[] patterns
    final Task task
    Object[] dependentTasks

    ConditionalDependency(Task task, Pattern[] patterns) {
        this.task = task
        this.patterns = patterns
    }

    public Task dependsOn(Object... args) {
        this.dependentTasks = args
        task.project.registerConditionalDependency(this)
        task
    }

}
