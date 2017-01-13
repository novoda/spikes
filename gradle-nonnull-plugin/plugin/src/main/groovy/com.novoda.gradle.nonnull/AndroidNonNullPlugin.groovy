package com.novoda.gradle.nonnull

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.StopExecutionException

public class AndroidNonNullPlugin implements Plugin<Project> {

    void apply(Project project) {

        if (project.plugins.hasPlugin("com.android.application")) {
            applyAndroid(project, project.android.applicationVariants)
            return
        }

        if (project.plugins.hasPlugin("com.android.library")) {
            applyAndroid(project, project.android.libraryVariants)
            return
        }

        throw new StopExecutionException("The 'android' plugin is required.")
    }

    private static void applyAndroid(project, variants) {
        variants.all { variant ->

            def task = project.task("generate${variant.name.capitalize()}NonNullAnnotations", type: GeneratePackageAnnotationsTask)
            task.outputDir = project.file("${project.buildDir}/generated/source/nonNull/${variant.dirName}")
            task.sourceSets = variant.sourceSets
            variant.registerJavaGeneratingTask(task, task.outputDir)
        }
    }
}
