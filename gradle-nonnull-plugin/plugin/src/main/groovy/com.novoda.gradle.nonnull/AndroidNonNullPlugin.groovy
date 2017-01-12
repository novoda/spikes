package com.novoda.gradle.nonnull

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.StopExecutionException

public class AndroidNonNullPlugin implements Plugin<Project> {

    void apply(Project project) {
        if (!project.plugins.hasPlugin('android')) {
            throw new StopExecutionException("The 'android' plugin is required.")
        }

        project.task('generateNonNullJavaFiles', dependsOn: "assembleDebug") {
            group = "Copying"
            description = "Generate package-info.java classes"

            def mainSrcPhrase = "src" + File.separatorChar + "main" + File.separatorChar + "java" + File.separatorChar

            def mainTestSrcPhrase = "src" + File.separatorChar + "test" + File.separatorChar +
                    "java" + File.separatorChar

            def mainAndroidTestSrcPhrase = "src" + File.separatorChar + "androidTest" + File.separatorChar +
                    "java" + File.separatorChar

            def sourceDirPath = "${project.projectDir}" + File.separatorChar + "src" + File.separatorChar +
                    "main" + File.separatorChar + "java" + File.separatorChar +
                    "com" + File.separatorChar + "novoda" + File.separatorChar + "gradle" + File.separatorChar + "nonnull"

            def sourceDir = project.file(sourceDirPath)

            def testSourceDir = project.file("${project.projectDir}" + File.separatorChar + "src" + File.separatorChar +
                    "test" + File.separatorChar + "java" + File.separatorChar +
                    "com" + File.separatorChar + "novoda" + File.separatorChar + "gradle" + File.separatorChar + "nonnull")

            def androidTestSourceDir = project.file("${project.projectDir}" + File.separatorChar + "src" + File.separatorChar +
                    "androidTest" + File.separatorChar + "java" + File.separatorChar +
                    "com" + File.separatorChar + "novoda" + File.separatorChar + "gradle" + File.separatorChar + "nonnull")

            generateInfoFiles(sourceDir, mainSrcPhrase, project);
            sourceDir.eachDirRecurse { dir ->
                generateInfoFiles(dir, mainSrcPhrase, project)
            }
            if (project.file(testSourceDir).exists()) {
                generateInfoFiles(testSourceDir, mainTestSrcPhrase, project);
                testSourceDir.eachDirRecurse { dir ->
                    generateInfoFiles(dir, mainTestSrcPhrase, project)
                }
            }
            if (project.file(androidTestSourceDir).exists()) {
                generateInfoFiles(androidTestSourceDir, mainAndroidTestSrcPhrase, project);
                androidTestSourceDir.eachDirRecurse { dir ->
                    generateInfoFiles(dir, mainAndroidTestSrcPhrase, project)
                }
            }
            println "[SUCCESS] NonNull generator: package-info.java files checked"
        }
    }

    private static void generateInfoFiles(File dir, String mainSrcPhrase, project) {
        def infoFileContentHeader = getFileContentHeader();
        def infoFileContentFooter = getFileContentFooter();
        def infoFilePath = dir.getAbsolutePath() + File.separatorChar + "package-info.java"

        //project.file(infoFilePath).delete(); //do not use in production code
        if (!project.file(infoFilePath).exists()) {
            def infoFileContentPackage = getFileContentPackage(dir.getAbsolutePath(), mainSrcPhrase);
            new File(infoFilePath).write(infoFileContentHeader +
                    infoFileContentPackage + infoFileContentFooter)
            println "[dir] " + infoFilePath + "  created";
        }
    }

    static def getFileContentPackage(String path, String mainSrcPhrase) {
        def mainSrcPhraseIndex = path.indexOf(mainSrcPhrase)
        def output = path.substring(mainSrcPhraseIndex)

        // Win hotfix
        if (System.properties['os.name'].toLowerCase().contains('windows')) {
            output = output.replace("\\", "/")
            mainSrcPhrase = mainSrcPhrase.replace("\\", "/")
        }

        return "package " + output.replaceAll(mainSrcPhrase, "").replaceAll(
                "/", ".") + ";\n"
    }

    static def getFileContentHeader() {
        return "/**\n" +
                " *\n" +
                " * Make all method parameters @NonNull by default.\n" +
                " *\n" +
                " * We assume that all method parameters and return types are NON-NULL by default.\n" +
                " *\n" +
                " * e.g.\n" +
                " *\n" +
                " * String trimExampleMethod(String value) {\n" +
                " *     return value.trim();\n" +
                " * }\n" +
                " *\n" +
                " * is equal to:\n" +
                " *\n" +
                " * @NonNull\n" +
                " * String trimExampleMethod(@NonNull String value) {\n" +
                " *     return value.trim();\n" +
                " * }\n" +
                " *\n" +
                " * reverse this behaviour with: @Nullable annotation.\n" +
                " *\n" +
                " */\n" +
                "@ParametersAreNonnullByDefault\n"
    }

    static def getFileContentFooter() {
        return "\n" +
                "import javax.annotation.ParametersAreNonnullByDefault;\n"
    }

}
