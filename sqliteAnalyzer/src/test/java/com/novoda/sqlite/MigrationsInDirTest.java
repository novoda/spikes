package com.novoda.sqlite;

import java.io.File;
import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class MigrationsInDirTest {

    private MigrationsInDir migrations;

    @Before
    public void setUp() throws Exception {
        String migrationsDir = MigrationsInDir.class.getResource("/migrations").getFile();
        migrations = new MigrationsInDir(new File(migrationsDir));
    }

    @Test
    public void shouldIterateInCorrectOrder() throws Exception {
        Iterator<File> iterator = migrations.asIterable().iterator();
        assertStartsWithNumber(iterator.next(), 1);
        assertStartsWithNumber(iterator.next(), 2);
        assertStartsWithNumber(iterator.next(), 3);
    }


    private void assertStartsWithNumber(File file, int number) {
        String name = file.getName();
        assertTrue(name+ " does not start with "+number, name.startsWith(Integer.toString(number)));
    }
}
