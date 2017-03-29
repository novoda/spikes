package com.novoda.tpbot.automation;

import com.novoda.support.ColonStringSplitter;

import org.junit.Before;
import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

public class ColonStringSplitterTest {

    private ColonStringSplitter colonStringSplitter;

    @Before
    public void setUp() {
        colonStringSplitter = new ColonStringSplitter();
    }

    @Test
    public void givenNullString_whenSplitting_thenReturnsEmptyStringArray() {
        String nullString = null;

        String[] result = colonStringSplitter.split(nullString);

        assertThat(result).isEmpty();
    }

    @Test
    public void givenStringContainingColons_whenSplitting_thenReturnsArraySplitByColon() {
        String stringContainingColons = "this:string:contains:colons";

        String[] result = colonStringSplitter.split(stringContainingColons);

        assertThat(result).isEqualTo(new String[]{
                "this",
                "string",
                "contains",
                "colons"
        });
    }

}
