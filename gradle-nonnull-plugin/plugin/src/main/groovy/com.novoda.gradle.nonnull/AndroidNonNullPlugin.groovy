package com.novoda.gradle.nonnull

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.FileVisitDetails
import org.gradle.api.tasks.StopExecutionException

public class AndroidNonNullPlugin implements Plugin<Project> {

    void apply(Project project) {
        if (!project.plugins.hasPlugin('android')) {
            throw new StopExecutionException("The 'android' plugin is required.")
        }

        project.task('generateNonNullJavaFiles', dependsOn: "assembleDebug") {
            group = "Copying"
            description = "Generate package-info.java classes"

            def outputDir = project.file("${project.buildDir}/generated/source/nonNull/main")


            def java = project.android.sourceSets.main.java


            Set<String> packages = []

            java.sourceFiles.visit { FileVisitDetails details ->
                if (details.file.file) {
                    //TODO respect multiple sourceDirs
                    def packagePath = details.file.parent.replace((java.srcDirs as List)[0].absolutePath, '')
                    packages << packagePath
                }
            }

            println(packages)

            packages.any { packagePath ->
                def dir = new File(outputDir, packagePath)
                dir.mkdirs()

                def file = new File(dir, "package-info.java")
                if (file.createNewFile()) {
                    file.append(getFileContentHeader())
                    file.append(getFileContentPackage(packagePath))
                    file.append(getFileContentFooter())
                }

                println(file.path)
            }

            println "[SUCCESS] NonNull generator: package-info.java files checked"
        }
    }

    static def getFileContentPackage(String path) {
        def packageName = path.replaceAll("/", ".").replaceFirst("\\.", "")

        return "package $packageName;\n"
    }

    static def getFileContentHeader() {
        return "/**\n" +
                " *\n" +
                " * Make all method parameters @NonNull by default.\n" +
                " *\n" +
                " * We assume that all method parameters are NON-NULL by default.\n" +
                " *\n" +
                " * e.g.\n" +
                " *\n" +
                " * void setValue(String value) {\n" +
                " *     this.value = value;\n" +
                " * }\n" +
                " *\n" +
                " * is equal to:\n" +
                " *\n" +
                " * void setValue(@NonNull String value) {\n" +
                " *     this.value = value;\n" +
                " * }\n" +
                " *\n" +
                " */\n" +
                "@ParametersAreNonnullByDefault\n"
    }

    static def getFileContentFooter() {
        return "\n" +
                "import javax.annotation.ParametersAreNonnullByDefault;\n"
    }

}
