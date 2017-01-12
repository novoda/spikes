package com.novoda.gradle.nonnull

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.StopExecutionException

public class AndroidNonNullPlugin implements Plugin<Project> {

    void apply(Project project) {

        def task = project.task("generateNonNullAnnotations", type: GeneratePackageAnnotationsTask)
        task.outputDir = project.file("${project.buildDir}/generated/source/nonNull/main")

        if (project.plugins.hasPlugin("com.android.application")) {
            applyAndroid(task, project.android.applicationVariants)
            return
        }

        if (project.plugins.hasPlugin("com.android.library")) {
            applyAndroid(task, project.android.libraryVariants)
            return
        }

        throw new StopExecutionException("The 'android' plugin is required.")
    }

    private static void applyAndroid(task, variants) {
        variants.all { variant ->
            variant.registerJavaGeneratingTask(task, task.outputDir)
        }
    }
}
