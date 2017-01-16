package com.novoda.gradle.nonnull

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.StopExecutionException
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.plugins.ide.idea.IdeaPlugin

public class AndroidNonNullPlugin implements Plugin<Project> {

    void apply(Project project) {

        if (project.plugins.hasPlugin('com.android.application')) {
            applyAndroid(project, project.android.applicationVariants)
            return
        }

        if (project.plugins.hasPlugin('com.android.library')) {
            applyAndroid(project, project.android.libraryVariants)
            return
        }

        if (project.plugins.hasPlugin('java')) {
            applyJava(project)
            return
        }

        throw new StopExecutionException("The 'android' or 'java' plugin is required.")
    }

    private static void applyAndroid(project, variants) {
        variants.all { variant ->

            def outputPath = "${project.buildDir}/generated/source/nonNull/${variant.dirName}"
            def sourceSets = variant.sourceSets

            def task = createTask(project, variant.name, outputPath, sourceSets)

            variant.registerJavaGeneratingTask(task, task.outputDir)
        }
    }

    private static createTask(project, taskName, outputPath, sourceSets) {
        def task = project.task("generate${taskName.capitalize()}NonNullAnnotations", type: GeneratePackageAnnotationsTask)
        task.outputDir = project.file(outputPath)
        task.sourceSets = sourceSets
        task
    }

    private static void applyJava(project) {
        project.sourceSets.all { sourceSet ->
            String sourceSetName = (String) sourceSet.name
            String taskName = "main".equals(sourceSetName) ? '' : sourceSetName

            def generatedSourcesDir = "${project.buildDir}/generated/source/nonNull/${sourceSet.name}"
            GeneratePackageAnnotationsTask task = createTask(project, taskName, generatedSourcesDir, [sourceSet])

            Task classesTask = project.tasks.getByName(taskName.isEmpty() ? "classes" : "${taskName}Classes")
            classesTask.mustRunAfter task

            JavaCompile compileTask =
                    (JavaCompile) project.tasks.getByName("compile${taskName.capitalize()}Java")
            compileTask.source += project.fileTree(task.outputDir)
            compileTask.dependsOn(task)

        }
        configureIdeaModule(project)
    }

    private static void configureIdeaModule(Project project) {
        // so the user does not need to apply it when using the plugin
        project.apply plugin: 'idea'

        project.sourceSets.all { sourceSet ->
            def generatedSourcesDir = new File("${project.buildDir}/generated/source/nonNull/${sourceSet.name}")
            project.plugins.withType(IdeaPlugin) {
                project.afterEvaluate {
                    project.idea.module {
                        def excl = []
                        for (File f = generatedSourcesDir; f != null && f != project.projectDir; f = f.parentFile) {
                            excl.add(f)
                        }

                        if (excl.contains(project.buildDir) && excludeDirs.contains(project.buildDir)) {
                            excludeDirs -= project.buildDir
                            // Race condition: many of these will actually be created afterwards…
                            def subdirs = project.buildDir.listFiles({ f -> f.directory } as FileFilter)
                            if (subdirs != null) {
                                excludeDirs += subdirs as List
                            }
                        }
                        excludeDirs -= excl

                        sourceDirs += generatedSourcesDir
                        generatedSourceDirs += generatedSourcesDir
                    }
                }
            }
        }

    }
}
