package com.novoda.staticanalysis

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class TestProjectRule implements TestRule {

    private File projectDir
    private GradleRunner gradleRunner

    @Override
    Statement apply(Statement base, Description description) {
        return new Statement() {
            @Override
            void evaluate() throws Throwable {
                projectDir =  createProjectDir("${System.currentTimeMillis()}")
                gradleRunner = GradleRunner.create()
                        .withProjectDir(projectDir)
                        .withDebug(true)
                        .withPluginClasspath()
                        .forwardStdOutput(new OutputStreamWriter(System.out))
                        .forwardStdError(new OutputStreamWriter(System.out))
                base.evaluate()
                projectDir.deleteDir()
                projectDir = null
                gradleRunner = null
            }
        }
    }

    private static File createProjectDir(String path) {
        File dir = new File(Fixtures.BUILD_DIR, "test-projects/$path")
        dir.deleteDir()
        dir.mkdirs()
        return dir
    }

    TestProjectRule withBuildScript(String buildScript) {
        new File(projectDir, 'build.gradle').text = buildScript
        return this
    }

    TestProjectRule withDefaultCheckstyleConfig() {
        return withFile(Fixtures.CHECKSTYLE_CONFIG, 'config/checkstyle/checkstyle.xml')
    }

    private TestProjectRule withFile(File source, String path) {
        File file = new File(projectDir, path)
        file.parentFile.mkdirs()
        file.text = source.text
        return this
    }

    BuildResult buildAndFail(String... arguments) {
        gradleRunner
                .withArguments(arguments)
                .buildAndFail()
    }

    BuildResult build(String... arguments) {
        gradleRunner
                .withArguments(arguments)
                .build()
    }

}
