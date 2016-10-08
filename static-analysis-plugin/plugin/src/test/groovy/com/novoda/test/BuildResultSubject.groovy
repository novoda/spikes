package com.novoda.test

import com.google.common.truth.FailureStrategy
import com.google.common.truth.Subject
import com.google.common.truth.SubjectFactory
import com.google.common.truth.Truth
import org.gradle.testkit.runner.BuildResult

import javax.annotation.Nullable;

final class BuildResultSubject extends Subject<BuildResultSubject, BuildResult> {

    private static final SubjectFactory<BuildResultSubject, BuildResult> FACTORY = new SubjectFactory<BuildResultSubject, BuildResult>() {
        @Override
        BuildResultSubject getSubject(FailureStrategy failureStrategy, BuildResult buildResult) {
            new BuildResultSubject(failureStrategy, buildResult)
        }
    }
    public static final String CHECKSTYLE_VIOLATIONS_LOG = 'Checkstyle rule violations were found.'

    public static BuildResultSubject assertThat(BuildResult buildResult) {
        Truth.assertAbout(FACTORY).that(buildResult)
    }

    BuildResultSubject(FailureStrategy failureStrategy, @Nullable BuildResult actual) {
        super(failureStrategy, actual)
    }

    public void containsCheckstyleViolations() {
        Truth.assertThat(actual().output).contains(CHECKSTYLE_VIOLATIONS_LOG)
    }

    public void doesNotContainCheckstyleViolationsLog() {
        Truth.assertThat(actual().output).doesNotContain(CHECKSTYLE_VIOLATIONS_LOG)
    }

}
