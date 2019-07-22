package com.novoda.gradle.diffdependencytask

class MemoryConditionalDependencyRepository implements ConditionalDependencyRepository {

    static {
        Map.metaClass.multiPut << { key, value ->
            delegate[key] = delegate[key] ?: []
            delegate[key].add(value)
        }
    }

    private Map<String, List<ConditionalDependency>> conditionalDependenciesMap = new LinkedHashMap<>()

    public void add(ConditionalDependency conditionalDependency) {
        conditionalDependency.patterns.toList().forEach {
            conditionalDependenciesMap.multiPut(it, conditionalDependency)
        }
    }

    @Override
    Map<String, List<ConditionalDependency>> getConditionalDependencies() {
        conditionalDependenciesMap
    }
}
