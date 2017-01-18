package com.novoda.gradle.nonnull

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.StopExecutionException
import org.gradle.api.tasks.compile.JavaCompile

public class AndroidNonNullPlugin implements Plugin<Project> {

    void apply(Project project) {

        if (project.plugins.hasPlugin('com.android.application')) {
            applyAndroid(project, project.android.applicationVariants)
        } else if (project.plugins.hasPlugin('com.android.library')) {
            applyAndroid(project, project.android.libraryVariants)
        } else if (project.plugins.hasPlugin('java')) {
            applyJava(project)
        } else {
            throw new StopExecutionException('The Android or Java plugin is required.')
        }
    }

    private static void applyAndroid(project, variants) {
        variants.all { variant ->

            def outputPath = "${project.buildDir}/generated/source/nonNull/${variant.dirName}"
            Task task = createTask(project, variant.name, outputPath, variant.sourceSets)

            variant.registerJavaGeneratingTask(task, task.outputDir)
        }
    }

    private static createTask(project, taskName, outputPath, sourceSets) {
        project.task("generate${taskName.capitalize()}NonNullAnnotations", type: GeneratePackageAnnotationsTask) {
            outputDir = project.file(outputPath)
            sourceSets = owner.sourceSets
        }
    }

    private static void applyJava(project) {
        configureTask(project)
        configureIdeaModule(project)
    }

    private static void configureTask(Project project) {
        project.sourceSets.all { sourceSet ->
            String sourceSetName = (String) sourceSet.name
            String taskName = "main".equals(sourceSetName) ? '' : sourceSetName

            def generatedSourcesDir = "${project.buildDir}/generated/source/nonNull/${sourceSet.name}"
            Task task = createTask(project, taskName, generatedSourcesDir, [sourceSet])

            Task classesTask = project.tasks.getByName(taskName.isEmpty() ? "classes" : "${taskName}Classes")
            classesTask.mustRunAfter task

            JavaCompile compileTask = (JavaCompile) project.tasks.getByName("compile${taskName.capitalize()}Java")
            compileTask.source += project.fileTree(task.outputDir)
            compileTask.dependsOn(task)

        }
    }

    // inspired by https://github.com/tbroyer/gradle-apt-plugin/blob/master/src/main/groovy/net/ltgt/gradle/apt/AptPlugin.groovy#L171-L213
    private static void configureIdeaModule(Project project) {
        // so the user does not need to apply it when using the plugin
        project.apply plugin: 'idea'

        project.sourceSets.all { sourceSet ->
            def generatedSourcesDir = new File("${project.buildDir}/generated/source/nonNull/${sourceSet.name}")

            project.afterEvaluate {
                project.idea.module {
                    def ancestors = getAncestors(generatedSourcesDir, project.projectDir)

                    if (ancestors.contains(project.buildDir) && excludeDirs.contains(project.buildDir)) {
                        excludeDirs -= project.buildDir
                        // Race condition: many of these will actually be created afterwards…
                        def subdirs = project.buildDir.listFiles({ f -> f.directory } as FileFilter)
                        if (subdirs != null) {
                            excludeDirs += subdirs as List
                        }
                    }
                    excludeDirs -= ancestors

                    sourceDirs += generatedSourcesDir
                    generatedSourceDirs += generatedSourcesDir
                }
            }
        }

    }

    private static List getAncestors(dir, rootDir) {
        def ancestors = []
        for (File f = dir; f != null && f != rootDir; f = f.parentFile) {
            ancestors.add(f)
        }
        ancestors
    }
}
