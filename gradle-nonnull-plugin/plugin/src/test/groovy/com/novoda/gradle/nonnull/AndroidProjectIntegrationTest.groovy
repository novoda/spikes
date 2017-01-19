package com.novoda.gradle.nonnull

import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.gradle.testkit.runner.internal.DefaultGradleRunner
import org.gradle.tooling.GradleConnector
import org.gradle.tooling.ProjectConnection
import org.gradle.tooling.model.idea.IdeaModule
import org.gradle.tooling.model.idea.IdeaProject
import org.junit.ClassRule
import org.junit.Test
import org.junit.rules.TestWatcher;
import org.junit.runner.Description
import org.junit.runners.model.Statement

import static com.google.common.truth.Truth.assertThat

public class AndroidProjectIntegrationTest {

    @ClassRule
    public static final ProjectRule PROJECT = new ProjectRule()

    @Test
    public void shouldGeneratePackageAnnotation() {
        PROJECT.generatedSrcDirs.each {
            def file = new File(it, 'package-info.java')
            assertThat(file.isFile()).isTrue()
        }
    }

    @Test
    public void shouldHaveAnnotationDefined() {
        PROJECT.generatedSrcDirs.each {
            def file = new File(it, 'package-info.java')
            assertThat(file.text).contains('@ParametersAreNonnullByDefault')
        }
    }

    @Test
    public void shouldConfigureIdeaModuleToMarkGeneratedFolderAsSource() {
        ProjectConnection connection = GradleConnector.newConnector()
                .forProjectDirectory(PROJECT.projectDir)
                .connect()

        def coreModule = connection.getModel(IdeaProject).modules[1]

        assertThat(generatedSourceDirectories(coreModule)).contains(projectFilePath('core/build/generated/source/nonNull/main'))
        assertThat(excludedDirectories(coreModule)).doesNotContain(projectFilePath('core/build'))
    }

    @Test
    public void shouldNotRunTaskWhenInputHasNotChanged() {
        def buildResult = PROJECT.runner.withArguments('core:assemble').build()
        def task = buildResult.task(':core:generateNonNullAnnotations')

        assertThat(task.outcome).isEqualTo(TaskOutcome.UP_TO_DATE)
    }

    @Test
    public void shouldRunTaskAgainWhenFilesInNewPackageCreated() {
        def tempClass = new File(PROJECT.tempDir, 'Temp.java')
        tempClass.write ''' package com.novoda.gradle.nonnull.temp;
                            public class Temp {}'''

        def buildResult = PROJECT.runner.withArguments('core:assemble').build()
        def task = buildResult.task(':core:generateNonNullAnnotations')

        assertThat(task.outcome).isEqualTo(TaskOutcome.SUCCESS)
    }

    private static String projectFilePath(String path) {
        new File(PROJECT.projectDir, path).canonicalPath
    }

    private static Set<?> generatedSourceDirectories(IdeaModule ideaModule) {
        ideaModule.contentRoots*.generatedSourceDirectories*.directory.canonicalPath.flatten()
    }

    private static Set<?> excludedDirectories(IdeaModule ideaModule) {
        ideaModule.contentRoots*.excludeDirectories*.canonicalPath.flatten()
    }

    static class ProjectRule extends TestWatcher {
        File projectDir
        File tempDir
        Set<File> generatedSrcDirs
        GradleRunner runner

        @Override
        protected void starting(Description description) {
            super.starting(description);

            projectDir = new File('../sample')
            tempDir = new File(projectDir, 'core/src/main/java/com/novoda/gradle/nonnull/temp')
            tempDir.mkdirs()

            runner = DefaultGradleRunner.create()
                    .withProjectDir(projectDir)
                    .withDebug(true)
                    .forwardStdOutput(new OutputStreamWriter(System.out))
                    .withArguments('clean', 'assembleCustomDebug', 'core:assemble', '-s')
            runner.build()

            File generatedAppRoot = new File(projectDir, 'app/build/generated/source/nonNull/custom/debug')
            File generatedCoreRoot = new File(projectDir, 'core/build/generated/source/nonNull/main')

            generatedSrcDirs = [
                    new File(generatedAppRoot, 'com/novoda/gradle/nonnull'),
                    new File(generatedAppRoot, 'com/novoda/gradle/nonnull/custom'),
                    new File(generatedAppRoot, 'com/novoda/gradle/common'),
                    new File(generatedCoreRoot, 'com/novoda/gradle/nonnull/core')
            ]
        }

        @Override
        protected void finished(Description description) {
            tempDir.deleteDir()
            super.finished(description);
        }
    }

}
