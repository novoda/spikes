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

    static class ProjectRule implements TestRule {
        File projectDir
        BuildResult buildResult
        Set<File> generatedSrcDirs

        @Override
        Statement apply(Statement base, Description description) {
            projectDir = new File('../sample')

            buildResult = DefaultGradleRunner.create()
                    .withProjectDir(projectDir)
                    .withDebug(true)
                    .forwardStdOutput(new OutputStreamWriter(System.out))
                    .withArguments('clean', 'assembleCustomDebug')
                    .build()

            File generatedRoot = new File(projectDir, 'app/build/generated/source/nonNull/custom/debug')

            generatedSrcDirs = [
                    new File(generatedRoot, 'com/novoda/gradle/nonnull'),
                    new File(generatedRoot, 'com/novoda/gradle/nonnull/custom'),
                    new File(generatedRoot, 'com/novoda/gradle/common')
            ]

            return base;
        }
    }

}
