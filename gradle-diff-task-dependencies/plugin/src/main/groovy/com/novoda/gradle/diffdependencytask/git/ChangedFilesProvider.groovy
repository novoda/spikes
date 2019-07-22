package com.novoda.gradle.diffdependencytask.git

class ChangedFilesProvider implements GroovyCallable<List<String>> {

    private final GroovyCallable<String> getCurrentBranch
    private final GroovyCallable<String> getTargetBranch

    private List<String> changedFiles

    static ChangedFilesProvider create() {
        return new ChangedFilesProvider(new CurrentBranchProvider(), new TargetBranchProvider())
    }

    ChangedFilesProvider(GroovyCallable<String> currentBranchProvider,
                         GroovyCallable<String> targetBranchProvider) {

        this.getCurrentBranch = currentBranchProvider
        this.getTargetBranch = targetBranchProvider
    }

    private List<String> getChangedFiles() {
        def currentBranch = getCurrentBranch()
        def targetBranch = getTargetBranch()
        def process = "git diff --name-only $currentBranch $targetBranch".execute()
        process.in.readLines()
    }

    @Override
    List<String> call() throws Exception {
        if (changedFiles == null) {
            try {
                changedFiles = getChangedFiles()
            } catch (Throwable t) {
                println "Could not get changed files because of exception \"$t\".\n" +
                        "Defaulting to empty list of changed files."
                changedFiles = []
            }
        }
        return changedFiles
    }

}
