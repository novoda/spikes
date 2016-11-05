package com.novoda.gradle.diffdependencytask.git

class CurrentBranchProvider implements GroovyCallable<String> {

    @Override
    String call() throws Exception {
        def process = "git rev-parse --abbrev-ref HEAD".execute()
        process.in.readLines()[0]
    }

}
