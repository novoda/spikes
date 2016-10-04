package com.novoda.staticanalysis

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

abstract class TestProjectRule implements TestRule {

    protected File projectDir
    protected GradleRunner gradleRunner

    static TestProjectRule forJavaProject() {
        new TestProjectRule() {
            @Override
            BuildScriptBuilder newBuildScriptBuilder() {
                BuildScriptBuilder.forJava()
            }
        }
    }

    static TestProjectRule forAndroidProject() {
        new TestProjectRule() {
            @Override
            protected BuildScriptBuilder newBuildScriptBuilder() {
                BuildScriptBuilder.forAndroid()
            }

            @Override
            protected void initProject() {
                super.initProject()
                copyFile(Fixtures.LOCAL_PROPERTIES, 'local.properties')
            }

            @Override
            protected List<String> defaultArguments() {
                ['-x', 'lint'] + super.defaultArguments()
            }
        }
    }

    protected TestProjectRule() {
    }

    @Override
    Statement apply(Statement base, Description description) {
        return new Statement() {
            @Override
            void evaluate() throws Throwable {
                initProject()
                base.evaluate()
                projectDir.deleteDir()
                projectDir = null
                gradleRunner = null
            }

        }
    }

    protected void initProject() {
        projectDir = createProjectDir("${System.currentTimeMillis()}")
        gradleRunner = GradleRunner.create()
                .withProjectDir(projectDir)
                .withDebug(true)
                .withPluginClasspath()
                .forwardStdOutput(new OutputStreamWriter(System.out))
                .forwardStdError(new OutputStreamWriter(System.out))
        copyFile(Fixtures.CHECKSTYLE_CONFIG, 'config/checkstyle/checkstyle.xml')
    }

    private static File createProjectDir(String path) {
        File dir = new File(Fixtures.BUILD_DIR, "test-projects/$path")
        dir.deleteDir()
        dir.mkdirs()
        return dir
    }

    protected abstract BuildScriptBuilder newBuildScriptBuilder()

    protected List<String> defaultArguments() {
        Collections.singletonList('--stacktrace')
    }

    TestProjectRule withBuildScript(String buildScript) {
        new File(projectDir, 'build.gradle').text = buildScript
        return this
    }

    BuildResult buildAndFail(String... arguments) {
        withArguments(arguments).buildAndFail()
    }

    BuildResult build(String... arguments) {
        withArguments(arguments).build()
    }

    private GradleRunner withArguments(String... arguments) {
        List<String> defaultArgs = defaultArguments()
        List<String> args = new ArrayList<>(arguments.size() + defaultArgs.size())
        args.addAll(defaultArgs)
        args.addAll(arguments)
        gradleRunner.withArguments(args)
    }

    protected TestProjectRule copyFile(File source, String path) {
        File file = new File(projectDir, path)
        file.parentFile.mkdirs()
        file.text = source.text
        return this
    }

}
