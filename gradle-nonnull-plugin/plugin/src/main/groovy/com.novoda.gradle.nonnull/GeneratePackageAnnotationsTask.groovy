package com.novoda.gradle.nonnull

import groovy.transform.Memoized
import org.gradle.api.DefaultTask
import org.gradle.api.file.FileVisitDetails
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.incremental.IncrementalTaskInputs

class GeneratePackageAnnotationsTask extends DefaultTask {

    @InputFiles
    Set<File> packages;

    @OutputDirectory
    File outputDir

    def sourceSets

    @TaskAction
    void generatePackageAnnotations(IncrementalTaskInputs inputs) {
        description = "Annotates the source packages with @ParametersAreNonnullByDefault"

        inputs.outOfDate { change ->
            def dir = change.file
            if (!dir.exists() || dir.directory) {
                dir.mkdirs()

                def file = new File(dir, 'package-info.java')
                def packagePath = outputDir.toPath().relativize(dir.toPath()).toString()
                def packageName = packagePath.replaceAll('/', '.')
                file.write(createAnnotationDefinition(packageName))
            }
        }

        inputs.removed { change ->
            project.delete(change.file)
        }
    }

    @Memoized
    Set<File> getPackages() {
        Set<String> packages = []

        sourceSets.each { sourceSet ->
            sourceSet.java.srcDirs.findAll {
                it.exists()
            }.each { srcDir ->

                project.fileTree(srcDir).visit { FileVisitDetails details ->
                    if (details.file.file) {
                        def packagePath = details.file.parent.replace(srcDir.absolutePath, '')
                        packages << new File(outputDir, packagePath)
                    }
                }
            }
        }
        packages
    }

    static def createAnnotationDefinition(packageName) {
        """ |/**
            | *
            | * Make all method parameters @NonNull by default.
            | *
            | * We assume that all method parameters are NON-NULL by default.
            | *
            | * e.g.
            | *
            | * void setValue(String value) {
            | *     this.value = value;
            | * }
            | *
            | * is equal to:
            | *
            | * void setValue(@NonNull String value) {
            | *     this.value = value;
            | * }
            | *
            | */
            |@ParametersAreNonnullByDefault
            |package ${packageName};
            |
            |import javax.annotation.ParametersAreNonnullByDefault;
            |""".stripMargin('|')
    }
}
