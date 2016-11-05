Gradle Diff Dependency Task
===========================

_Gradle plugin to apply conditional task dependency according to `git diff` files._

## How to use

The Gradle plugin extends the `Task` Gradle API with one extra method: `ifDiffMatches`.
This method accepts a list of `Pattern`s that, if matched by any of the changed files in Git Diff, sets the task 
dependency.

For example:

```gradle
apply plugin: DiffDependencyTaskPlugin

task('testAll')
    .dependsOn('prepareTest') // regular dependency                       
    .ifDiffMatches(~'android/.*').dependsOn('testAndroid') // conditional dependency
    .ifDiffMatches(~'apple/.*').dependsOn('testApple') // conditional dependency
```

## Configuration

By default, the plugin uses:

* `git rev-parse` to retrieve the current branch
* the `ghprbTargetBranch` environment variable to retrieve the target branch
* `git diff` to retrieve the list of changed files between the current and the target branch

If you want to change the way the plugin must behave, you can set the `diffDependency.changedFilesProvider` closure
so that it returns a custom list of files.

For example, if you want to use a different environment variable to get the target branch, you can set the provider
to be:

```gradle
diffDependency {
    changedFilesProvider = new ChangedFilesProvider(
        new CurrentBranchProvider(), 
        { System.getenv('anotherTargetBranch') }
    )
}
```

You can change the current branch provider in the same way as before, or alter the logic to retrieve changed files 
altogether.
