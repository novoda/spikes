package com.novoda.gradle.diffdependencytask.git

import org.gradle.api.GradleException

class TargetBranchProvider implements GroovyCallable<String> {

    @Override
    String call() throws Exception {
        def targetBranch = System.getenv('ghprbTargetBranch')
        if (!targetBranch) {
            throw new GradleException("No target branch found.")
        }
        targetBranch
    }

}
