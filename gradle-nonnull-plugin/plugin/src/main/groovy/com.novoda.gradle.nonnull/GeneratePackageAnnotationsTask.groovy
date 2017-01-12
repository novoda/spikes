package com.novoda.gradle.nonnull

import org.gradle.api.DefaultTask
import org.gradle.api.file.FileVisitDetails
import org.gradle.api.tasks.TaskAction

class GeneratePackageAnnotationsTask extends DefaultTask {

    def outputDir

    def variant

    @TaskAction
    void generatePackageAnnotations() {
        description = "Generate package-info.java classes"

        variant.sourceSets.any {
            Set<String> packages = []

            it.java.srcDirs.findAll {
                it.exists()
            }.any { srcDir ->
                project.fileTree(srcDir).visit { FileVisitDetails details ->
                    if (details.file.file) {
                        def packagePath = details.file.parent.replace(srcDir.absolutePath, '')
                        packages << packagePath
                    }
                }
            }

            packages.any { packagePath ->
                def dir = new File(outputDir, packagePath)
                dir.mkdirs()

                def file = new File(dir, "package-info.java")
                if (file.createNewFile()) {
                    file.write(getFileContent(packagePath))
                }

                println(file.path)
            }
        }

        println "[SUCCESS] NonNull generator: package-info.java files checked"
    }

    static def getFileContent(String path) {
        def packageName = path.replaceAll("/", ".").replaceFirst("\\.", "")
        return """  |/**
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
                    |package $packageName;
                    |
                    |import javax.annotation.ParametersAreNonnullByDefault;
                    |""".stripMargin('|')
    }
}
