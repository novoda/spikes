package com.novoda.test

import com.google.common.truth.FailureStrategy
import com.google.common.truth.Subject
import com.google.common.truth.SubjectFactory
import com.google.common.truth.Truth

import javax.annotation.Nullable

import static com.novoda.test.TestProject.Result.Logs;

class LogsSubject extends Subject<LogsSubject, Logs> {
    private static final String VIOLATIONS_LIMIT_EXCEEDED = "Violations limit exceeded"
    private static final String CHECKSTYLE_VIOLATIONS_FOUND = "Checkstyle rule violations were found"
    private static final SubjectFactory<LogsSubject, Logs> FACTORY = new SubjectFactory<LogsSubject, Logs>() {
        @Override
        LogsSubject getSubject(FailureStrategy failureStrategy, Logs logs) {
            new LogsSubject(failureStrategy, logs)
        }
    }

    public static LogsSubject assertThat(Logs logs) {
        Truth.assertAbout(FACTORY).that(logs)
    }

    LogsSubject(FailureStrategy failureStrategy, @Nullable Logs actual) {
        super(failureStrategy, actual)
    }

    public void doesNotContainLimitExceeded() {
        Truth.assertThat(actual().output).doesNotContain(VIOLATIONS_LIMIT_EXCEEDED)
    }

    public void containsLimitExceeded(int errors, int warnings) {
        Truth.assertThat(actual().output).contains("$VIOLATIONS_LIMIT_EXCEEDED by $errors errors, $warnings warnings.")
    }

    public void doesNotContainCheckstyleViolations() {
        Truth.assertThat(actual().output).doesNotContain(CHECKSTYLE_VIOLATIONS_FOUND)
    }

    public void containsCheckstyleViolations(int errors, int warnings, File... reports) {
        def output = Truth.assertThat(actual().output)
        output.contains("$CHECKSTYLE_VIOLATIONS_FOUND ($errors errors, $warnings warnings). See the reports at:\n")
        for (File report : reports) {
            output.contains(report.path)
        }
    }
}
