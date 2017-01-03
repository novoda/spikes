package com.novoda.gradle.diffdependencytask

import java.util.regex.Pattern

class ConditionalDependencyEvaluator implements GroovyCallable<Set<ConditionalDependency>> {

    private final GroovyCallable<List<String>> changedFilesProvider
    private final ConditionalDependencyRepository repository

    public ConditionalDependencyEvaluator(GroovyCallable<List<String>> changedFilesProvider,
                                          ConditionalDependencyRepository repository) {

        this.changedFilesProvider = changedFilesProvider
        this.repository = repository
    }

    @Override
    Set<ConditionalDependency> call() throws Exception {
        def changedFiles = changedFilesProvider()
        def dependencyMap = repository.getConditionalDependencies()
        def matchedDependencies = new HashSet<ConditionalDependency>()

        dependencyMap.keySet().each {
            if (changedFilesContainPattern(changedFiles, it)) {
                matchedDependencies.addAll(dependencyMap[it])
            }
        }

        return matchedDependencies
    }

    private static boolean changedFilesContainPattern(List<String> changedFiles, Pattern pattern) {
        changedFiles.findResult { file ->
            file.matches(pattern) ? true : null
        }
    }
}
