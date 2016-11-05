package com.novoda.gradle.diffdependencytask

import java.util.regex.Pattern

interface ConditionalDependencyRepository {

    void add(ConditionalDependency conditionalDependency)

    Map<Pattern, List<ConditionalDependency>> getConditionalDependencies()

}
