package com.novoda.sqlite.impl;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class TableCreateStatmentParserTest {

    private static final String SIMPLE_JOIN = "CREATE VIEW view AS SELECT _ID FROM PROGRAMS INNER JOIN VIDEOS ON PROGRAMS.DEFAULT_VIDEO_ID = VIDEOS.REF_ID";
    private static final String DOUBLE_JOIN = SIMPLE_JOIN + " inner join tests";

    @Test
    public void testParsingSimpleJoin() throws Exception {
        List<String> involvedTables = new TableCreateStatementParser().parseUsedTables(SIMPLE_JOIN);
        Assert.assertArrayEquals(new String[]{"PROGRAMS", "VIDEOS"}, involvedTables.toArray());
    }

    @Test
    public void testParsingDoubleJoin() throws Exception {
        List<String> involvedTables = new TableCreateStatementParser().parseUsedTables(DOUBLE_JOIN);
        Assert.assertArrayEquals(new String[]{"PROGRAMS", "VIDEOS", "TESTS"}, involvedTables.toArray());
    }

}
