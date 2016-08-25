package com.novoda.buildproperties

import org.junit.Test

import static com.novoda.buildproperties.EntrySubject.assertThat

class EntryTest {

    private static final RuntimeException EXCEPTION_1 = new RuntimeException("exception 1")
    private static final RuntimeException EXCEPTION_2 = new RuntimeException("exception 2")

    @Test
    public void shouldReturnFirstEntryValueInOrWhenFirstEntryValueDoesNotThrow() {
        Entry entry1 = new Entry('key1', { 'value1' })
        Entry entry2 = new Entry('key2', { 'value2' })

        Entry entryWithFallback = entry1.or(entry2)

        assertThat(entryWithFallback).hasValue('value1')
    }

    @Test
    public void shouldReturnSecondEntryValueInOrWhenFirstEntryValueThrows() {
        Entry entry1 = new Entry('key1', { throw EXCEPTION_1 })
        Entry entry2 = new Entry('key2', { 'value2' })

        Entry entryWithFallback = entry1.or(entry2)

        assertThat(entryWithFallback).hasValue('value2')
    }

    @Test
    public void shouldThrowWhenFirstAndSecondEntryValueThrow() {
        Entry entry1 = new Entry('key1', { throw EXCEPTION_1 })
        Entry entry2 = new Entry('key2', { throw EXCEPTION_2 })

        Entry entryWithFallback = entry1.or(entry2)

        assertThat(entryWithFallback).willThrow(CompositeException.from(EXCEPTION_1).add(EXCEPTION_2))
    }

    @Test
    public void shouldReturnEvaluatedClosureWhenFirstEntryValueThrows() {
        Entry entry = new Entry('key', { throw EXCEPTION_1 })
        def fallback = { 'fallback' }

        Entry entryWithFallback = entry.or(fallback)

        assertThat(entryWithFallback).hasValue('fallback')
    }

    @Test
    public void shouldReturnValueWhenFirstEntryValueThrows() {
        Entry entry = new Entry('key', { throw EXCEPTION_1 })

        Entry entryWithFallback = entry.or('fallback')

        assertThat(entryWithFallback).hasValue('fallback')
    }

}
