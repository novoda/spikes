package com.novoda.gradle.diffdependencytask

import org.gradle.api.Task

import java.util.regex.Pattern

public class ConditionalDependency {

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
