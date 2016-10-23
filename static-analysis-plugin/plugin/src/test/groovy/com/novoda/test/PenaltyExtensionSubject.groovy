package com.novoda.test

import com.google.common.truth.FailureStrategy
import com.google.common.truth.Subject
import com.google.common.truth.SubjectFactory
import com.novoda.staticanalysis.PenaltyExtension

import javax.annotation.Nullable

import static com.google.common.truth.Truth.assertAbout
import static com.google.common.truth.Truth.assertThat;

class PenaltyExtensionSubject extends Subject<PenaltyExtensionSubject, PenaltyExtension> {

    private static final SubjectFactory<PenaltyExtensionSubject, PenaltyExtension> FACTORY = newFactory()
    private static SubjectFactory<PenaltyExtensionSubject, PenaltyExtension> newFactory() {
        new SubjectFactory<PenaltyExtensionSubject, PenaltyExtension>() {
            @Override
            PenaltyExtensionSubject getSubject(FailureStrategy failureStrategy, PenaltyExtension extension) {
                new PenaltyExtensionSubject(failureStrategy, extension)
            }
        }
    }

    private PenaltyExtensionSubject(FailureStrategy failureStrategy, @Nullable PenaltyExtension actual) {
        super(failureStrategy, actual)
    }

    public static PenaltyExtensionSubject assertThat(PenaltyExtension extension) {
        assertAbout(FACTORY).that(extension)
    }

    public void hasMaxWarnings(int maxWarnings) {
        assertThat(actual().maxWarnings).isEqualTo(maxWarnings)
    }

    public void hasMaxErrors(int maxErrors) {
        assertThat(actual().maxErrors).isEqualTo(maxErrors)
    }

}
