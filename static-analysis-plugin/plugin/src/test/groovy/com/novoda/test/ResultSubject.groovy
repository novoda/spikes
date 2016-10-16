package com.novoda.test

import com.google.common.truth.FailureStrategy
import com.google.common.truth.Subject
import com.google.common.truth.SubjectFactory
import com.google.common.truth.Truth
import com.novoda.test.TestProject.Result

import javax.annotation.Nullable;

class ResultSubject extends Subject<ResultSubject, Result> {
    private static final String CHECKSTYLE_VIOLATIONS_LOG = 'Checkstyle rule violations were found'

    private static final SubjectFactory<ResultSubject, Result> FACTORY = new SubjectFactory<ResultSubject, Result>() {
        @Override
        ResultSubject getSubject(FailureStrategy failureStrategy, Result result) {
            new ResultSubject(failureStrategy, result)
        }
    }

    public static ResultSubject assertThat(Result result) {
        Truth.assertAbout(FACTORY).that(result)
    }

    ResultSubject(FailureStrategy failureStrategy, @Nullable Result actual) {
        super(failureStrategy, actual)
    }

    public void containsCheckstyleViolationsLog() {
        Truth.assertThat(actual().output).contains(CHECKSTYLE_VIOLATIONS_LOG)
    }

    public void doesNotContainCheckstyleViolationsLog() {
        Truth.assertThat(actual().output).doesNotContain(CHECKSTYLE_VIOLATIONS_LOG)
    }
}
