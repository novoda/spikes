package com.novoda.gradle.diffdependencytask

import org.gradle.api.Task

import java.util.regex.Pattern

public class ConditionalDependency {

    final List<Pattern> patterns
    final Task task
    Object[] dependentTasks

    ConditionalDependency(Task task, List<Pattern> patterns, Object... tasks) {
        this.task = task
        this.patterns = patterns
        this.dependentTasks = tasks
    }

}
