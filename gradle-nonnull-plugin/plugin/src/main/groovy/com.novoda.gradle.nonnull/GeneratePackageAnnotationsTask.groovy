package com.novoda.gradle.nonnull

import groovy.transform.Memoized
import org.gradle.api.DefaultTask
import org.gradle.api.file.FileVisitDetails
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

class GeneratePackageAnnotationsTask extends DefaultTask {

    @Input
    Set<String> packages;

    @OutputDirectory
    def outputDir

    def sourceSets

    @TaskAction
    void generatePackageAnnotations() {
        description = "Annotates the source packages with @ParametersAreNonnullByDefault"

        Set<String> packages = getPackages()
        packages.each { packagePath ->
            def dir = new File(outputDir, packagePath)
            dir.mkdirs()

            def file = new File(dir, 'package-info.java')
            if (file.createNewFile()) {
                def packageName = packagePath.replaceAll('/', '.')
                file.write(createAnnotationDefinition(packageName))
            }
        }
    }

    @Memoized
    Set<String> getPackages() {
        Set<String> packages = []

        sourceSets.each { sourceSet ->
            sourceSet.java.srcDirs.findAll {
                it.exists()
            }.each { File srcDir ->
                project.fileTree(srcDir).visit { FileVisitDetails details ->
                    def file = details.file
                    if (file.file) {
                        def packagePath = srcDir.toPath().relativize(file.parentFile.toPath()).toString()
                        packages << packagePath
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
