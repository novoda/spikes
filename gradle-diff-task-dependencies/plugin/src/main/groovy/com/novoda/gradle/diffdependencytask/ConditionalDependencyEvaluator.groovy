package com.novoda.gradle.diffdependencytask

import java.util.regex.Pattern

class ConditionalDependencyEvaluator implements GroovyCallable<Void> {

    private final GroovyCallable<List<String>> changedFilesProvider
    private final ConditionalDependencyRepository repository

    public ConditionalDependencyEvaluator(GroovyCallable<List<String>> changedFilesProvider,
                                          ConditionalDependencyRepository repository) {

        this.changedFilesProvider = changedFilesProvider
        this.repository = repository
    }

    @Override
    Void call() throws Exception {
        def changedFiles = changedFilesProvider()
        def dependencyMap = repository.conditionalDependencies
        def matchedDependencies = new HashSet<ConditionalDependency>()

        dependencyMap.keySet().forEach {
            if (changedFilesContainPattern(changedFiles, it)) {
                matchedDependencies.addAll(dependencyMap[it])
            }
        }

        matchedDependencies.forEach {
            it.task.dependsOn(it.dependentTasks)
        }
    }

    private static boolean changedFilesContainPattern(List<String> changedFiles, Pattern pattern) {
        changedFiles.findResult { file ->
            file.matches(pattern) ? true : null
        }
    }
}
