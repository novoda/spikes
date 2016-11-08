package com.novoda.test

import com.google.common.truth.FailureStrategy
import com.google.common.truth.StringSubject
import com.google.common.truth.Subject
import com.google.common.truth.SubjectFactory
import com.google.common.truth.Truth
import com.google.common.truth.TruthJUnit

import javax.annotation.Nullable

import static com.novoda.test.TestProject.Result.Logs;

class LogsSubject extends Subject<LogsSubject, Logs> {
    private static final String VIOLATIONS_LIMIT_EXCEEDED = "Violations limit exceeded"
    private static final String CHECKSTYLE_VIOLATIONS_FOUND = "Checkstyle rule violations were found"
    private static final String PMD_VIOLATIONS_FOUND = "PMD rule violations were found"
    private static final String FINDBUGS_VIOLATIONS_FOUND = "Findbugs rule violations were found"
    private static final SubjectFactory<LogsSubject, Logs> FACTORY = new SubjectFactory<LogsSubject, Logs>() {
        @Override
        LogsSubject getSubject(FailureStrategy failureStrategy, Logs logs) {
            new LogsSubject(failureStrategy, logs)
        }
    }

    public static LogsSubject assertThat(Logs logs) {
        Truth.assertAbout(FACTORY).that(logs)
    }

    public static LogsSubject assumeThat(Logs logs) {
        TruthJUnit.assume().about(FACTORY).that(logs)
    }

    LogsSubject(FailureStrategy failureStrategy, @Nullable Logs actual) {
        super(failureStrategy, actual)
    }

    private StringSubject getOutputSubject() {
        check().that(actual().output)
    }

    public void doesNotContainLimitExceeded() {
        outputSubject.doesNotContain(VIOLATIONS_LIMIT_EXCEEDED)
    }

    public void containsLimitExceeded(int errors, int warnings) {
        outputSubject.contains("$VIOLATIONS_LIMIT_EXCEEDED by $errors errors, $warnings warnings.")
    }

    public void doesNotContainCheckstyleViolations() {
        outputSubject.doesNotContain(CHECKSTYLE_VIOLATIONS_FOUND)
    }

    public void doesNotContainPmdViolations() {
        outputSubject.doesNotContain(PMD_VIOLATIONS_FOUND)
    }

    public void doesNotContainFindbugsViolations() {
        outputSubject.doesNotContain(FINDBUGS_VIOLATIONS_FOUND)
    }

    public void containsCheckstyleViolations(int errors, int warnings, File... reports) {
        outputSubject.contains("$CHECKSTYLE_VIOLATIONS_FOUND ($errors errors, $warnings warnings). See the reports at:\n")
        for (File report : reports) {
            outputSubject.contains(report.path)
        }
    }

    public void containsPmdViolations(int errors, int warnings, File... reports) {
        outputSubject.contains("$PMD_VIOLATIONS_FOUND ($errors errors, $warnings warnings). See the reports at:\n")
        for (File report : reports) {
            outputSubject.contains(report.path)
        }
    }

    public void containsFindbugsViolations(int errors, int warnings, File... reports) {
        outputSubject.contains("$FINDBUGS_VIOLATIONS_FOUND ($errors errors, $warnings warnings). See the reports at:\n")
        for (File report : reports) {
            outputSubject.contains(report.path)
        }
    }
}
