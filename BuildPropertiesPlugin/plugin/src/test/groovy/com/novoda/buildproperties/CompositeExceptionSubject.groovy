package com.novoda.buildproperties

import com.google.common.truth.FailureStrategy
import com.google.common.truth.Subject
import com.google.common.truth.SubjectFactory
import com.google.common.truth.Truth

import javax.annotation.Nullable

final class CompositeExceptionSubject extends Subject<CompositeExceptionSubject, CompositeException> {

    private static
    final SubjectFactory<CompositeExceptionSubject, CompositeException> FACTORY = new SubjectFactory<CompositeExceptionSubject, CompositeException>() {
        @Override
        CompositeExceptionSubject getSubject(FailureStrategy fs, CompositeException that) {
            new CompositeExceptionSubject(fs, that)
        }
    }

    public static CompositeExceptionSubject assertThat(CompositeException compositeException) {
        Truth.assertAbout(FACTORY).that(compositeException)
    }

    private CompositeExceptionSubject(FailureStrategy failureStrategy,
                                      @Nullable CompositeException subject) {
        super(failureStrategy, subject)
    }

    public void contains(Throwable... throwables) {
        Truth.assertThat(subject.exceptions).containsExactly(throwables)
    }

    public void hasMessage(String... messages) {
        Truth.assertThat(subject.exceptions.collect { it.message }).containsExactly(messages)
    }

}
