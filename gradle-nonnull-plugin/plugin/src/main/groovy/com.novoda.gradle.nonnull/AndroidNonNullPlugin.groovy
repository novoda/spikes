package com.novoda.gradle.nonnull

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.StopExecutionException

public class AndroidNonNullPlugin implements Plugin<Project> {

    void apply(Project project) {
        if (!project.plugins.hasPlugin('android')) {
            throw new StopExecutionException("The 'android' plugin is required.")
        }

        def task = project.task("generateNonNullAnnotations", type: GeneratePackageAnnotationsTask)
        task.outputDir = project.file("${project.buildDir}/generated/source/nonNull/main")

        project.android.applicationVariants.all { variant ->
            variant.registerJavaGeneratingTask(task, task.outputDir)
        }
    }
}
