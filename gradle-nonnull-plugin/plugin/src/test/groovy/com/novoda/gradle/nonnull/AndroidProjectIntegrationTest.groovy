package com.novoda.gradle.nonnull

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.internal.DefaultGradleRunner
import org.junit.ClassRule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

import static com.google.common.truth.Truth.assertThat

public class AndroidProjectIntegrationTest {

    @ClassRule
    public static final ProjectRule PROJECT = new ProjectRule()

    @Test
    public void shouldGeneratePackageAnnotation() {
        def fileExists = new File(PROJECT.generatedSrcDir, 'package-info.java').isFile()
        assertThat(fileExists).isTrue()
    }

    @Test
    public void shouldHaveAnnotationDefined() {
        def file = new File(PROJECT.generatedSrcDir, 'package-info.java')
        assertThat(file.text).contains('@ParametersAreNonnullByDefault')
    }

    static class ProjectRule implements TestRule {
        File projectDir
        BuildResult buildResult
        File generatedSrcDir

        @Override
        Statement apply(Statement base, Description description) {
            projectDir = new File('../sample')
            generatedSrcDir = new File(projectDir, 'app/build/generated/source/nonNull/custom/debug/com/novoda/gradle/nonnull')

            generatedSrcDir.deleteDir()

            buildResult = DefaultGradleRunner.create()
                    .withProjectDir(projectDir)
                    .withDebug(true)
                    .forwardStdOutput(new OutputStreamWriter(System.out))
                    .withArguments('assembleCustomDebug')
                    .build()

            return base;
        }
    }

}
