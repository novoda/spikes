package com.novoda.sqlite;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class NumberedFilesComparatorTest {

    private NumberedFilesComparator comparator;

    @Before
    public void setUp() throws Exception {
        comparator = new NumberedFilesComparator();
    }

    @Test
    public void shouldOrderFilesStartingWithNumber() {
        assertGreater("3_xxx", "2_ooo");
    }

    @Test
    public void shouldOrderByNumberNotLexicographically() {
        assertSmaller("1_aaa", "2_ooo");
        assertGreater("13_xxx", "2_ooo");
    }

    @Test
    public void shouldTreatNonNumberedFilesAsNumberedZero() throws Exception {
        assertGreater("1_ccc", "notnumbered");
    }

    private int compare(String left, String right) {
        return comparator.compare(new File(left), new File(right));
    }

    private void assertGreater(String left, String right) {
        assertTrue(left + " should be greater than " + right, compare(left, right) > 0);
        assertTrue(left + " should be smaller than " + right, compare(right, left) < 0);
    }

    private void assertSmaller(String left, String right) {
        assertTrue(right + " should be greater than " + left, compare(right, left) > 0);
        assertTrue(right + " should be smaller than " + left, compare(left, right) < 0);
    }
}
