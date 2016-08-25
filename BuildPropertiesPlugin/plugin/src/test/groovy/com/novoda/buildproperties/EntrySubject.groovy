package com.novoda.buildproperties

import com.google.common.truth.FailureStrategy
import com.google.common.truth.Subject
import com.google.common.truth.SubjectFactory
import com.google.common.truth.Truth

import javax.annotation.Nullable

final class EntrySubject extends Subject<EntrySubject, Entry> {

    private static final SubjectFactory<EntrySubject, Entry> FACTORY = new SubjectFactory<EntrySubject, Entry>() {
        @Override
        EntrySubject getSubject(FailureStrategy fs, Entry that) {
            new EntrySubject(fs, that)
        }
    }

    public static EntrySubject assertThat(Entry entry) {
        Truth.assertAbout(FACTORY).that(entry)
    }

    private EntrySubject(FailureStrategy failureStrategy, @Nullable Entry subject) {
        super(failureStrategy, subject)
    }

    public void willThrow(Class<? extends Throwable> throwableClass) {
        try {
            subject.value
            fail('throws', throwableClass)
        } catch (Throwable throwable) {
            Truth.assertThat(throwable).isInstanceOf(throwableClass)
        }
    }

    public void willThrow(CompositeException compositeException) {
        try {
            subject.value
            fail('throws', compositeException)
        } catch (CompositeException thrown) {
            Truth.assertThat(thrown.message).isEqualTo(compositeException.message)
        }
    }

    public void hasValue(def expected) {
        Truth.assertThat(subject.value).isEqualTo(expected)
    }

}
