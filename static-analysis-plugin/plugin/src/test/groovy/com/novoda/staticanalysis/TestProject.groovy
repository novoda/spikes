package com.novoda.staticanalysis

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner;

final class TestProject {

    private final File projectDir
    private final GradleRunner gradleRunner

    static TestProject create(String path) {
        File projectDir = createProjectDir(path)
        GradleRunner gradleRunner = GradleRunner.create()
                .withProjectDir(projectDir)
                .withDebug(true)
                .withPluginClasspath()
                .forwardStdOutput(new OutputStreamWriter(System.out))
                .forwardStdError(new OutputStreamWriter(System.out))
        new TestProject(projectDir, gradleRunner)
    }

    private static File createProjectDir(String path) {
        File dir = new File(Fixtures.BUILD_DIR, "test-projects/$path")
        dir.deleteDir()
        dir.mkdirs()
        return dir
    }

    private TestProject(File projectDir, GradleRunner gradleRunner) {
        this.projectDir = projectDir
        this.gradleRunner = gradleRunner
    }

    TestProject withBuildScript(String buildScript) {
        new File(projectDir, 'build.gradle').text = buildScript
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
