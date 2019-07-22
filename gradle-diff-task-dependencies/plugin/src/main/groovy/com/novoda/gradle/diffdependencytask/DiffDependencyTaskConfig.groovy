package com.novoda.gradle.diffdependencytask

import com.novoda.gradle.diffdependencytask.git.ChangedFilesProvider

class DiffDependencyTaskConfig {

    public GroovyCallable<List<String>> changedFilesProvider = ChangedFilesProvider.create()

    public ConditionalDependencyRepository conditionalDependencyRepository = new MemoryConditionalDependencyRepository()

}
