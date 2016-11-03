package com.novoda.test

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner

abstract class TestProject {
    private static final Closure<String> EXTENSION_TEMPLATE = { TestProject project ->
        """
staticAnalysis {
    ${(project.penalty ?: '').replace('            ', '')}
    ${(project.checkstyle ?: '').replace('        ', '    ')}
}
"""
    }

    private final File projectDir
    private final GradleRunner gradleRunner
    private final Closure<String> template
    Map<String, List<File>> sourceSets = [main: []]
    String penalty
    String checkstyle

    TestProject(Closure<String> template) {
        this.template = template
        this.projectDir = createProjectDir("${System.currentTimeMillis()}")
        this.gradleRunner = GradleRunner.create()
                .withProjectDir(projectDir)
                .withDebug(true)
                .withPluginClasspath()
                .forwardStdOutput(new OutputStreamWriter(System.out))
                .forwardStdError(new OutputStreamWriter(System.out))
    }

    private static File createProjectDir(String path) {
        File dir = new File(Fixtures.BUILD_DIR, "test-projects/$path")
        dir.deleteDir()
        dir.mkdirs()
        return dir
    }

    List<String> defaultArguments() {
        Collections.emptyList()
    }

    public TestProject withFile(File source, String path) {
        write(source.text, path)
        return this
    }

    public TestProject withFile(String text, String path) {
        write(text, path)
        return this
    }

    private void write(String text, String path) {
        File file = new File(gradleRunner.projectDir, path)
        file.parentFile.mkdirs()
        file.text = text
    }

    public TestProject withSourceSet(String sourceSet, File... srcDirs) {
        sourceSets[sourceSet] = srcDirs
        return this
    }

    public TestProject withPenalty(String penalty) {
        this.penalty = "penalty $penalty"
        return this
    }

    public TestProject withCheckstyle(String checkstyle) {
        this.checkstyle = checkstyle
        return this
    }

    public Result build(String... arguments) {
        BuildResult buildResult = newRunner(arguments).build()
        createResult(buildResult)
    }

    private GradleRunner newRunner(String... arguments) {
        withFile(Fixtures.Checkstyle.MODULES, 'config/checkstyle/checkstyle.xml')
        new File(projectDir, 'build.gradle').text = template.call(this)
        List<String> defaultArgs = defaultArguments()
        List<String> args = new ArrayList<>(arguments.size() + defaultArgs.size())
        args.addAll(defaultArgs)
        args.addAll(arguments)
        gradleRunner.withArguments(args)
    }

    private createResult(BuildResult buildResult) {
        new Result(buildResult, new File(projectDir, 'build'))
    }

    public Result buildAndFail(String... arguments) {
        BuildResult buildResult = newRunner(arguments).buildAndFail()
        createResult(buildResult)
    }

    public void deleteDir() {
        projectDir.deleteDir()
    }

    protected static String formatExtension(TestProject project) {
        EXTENSION_TEMPLATE.call(project)
    }

    public static class Result {
        private final BuildResult buildResult
        private final File buildDir

        Result(BuildResult buildResult, File buildDir) {
            this.buildResult = buildResult
            this.buildDir = buildDir
        }

        Logs getLogs() {
            new Logs(buildResult.output)
        }

        File buildFile(String path) {
            new File(buildDir, path)
        }

        public static class Logs {
            final String output

            Logs(String output) {
                this.output = output
            }
        }
    }
}
