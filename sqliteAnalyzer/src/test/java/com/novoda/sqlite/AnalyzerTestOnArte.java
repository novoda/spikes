package com.novoda.sqlite;

import com.novoda.sqlite.model.Database;
import com.novoda.sqlite.model.Table;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class AnalyzerTestOnArte {

    private Analyzer analyzer;
    private Database database;

    @Before
    public void setUp() throws Exception {
        String migrationsDir = MigrationsInDir.class.getResource("/arte_migrations").getFile();
        MigrationsInDir migrations = new MigrationsInDir(new File(migrationsDir));
        Migrator migrator = new Migrator(migrations);
        analyzer = new Analyzer(migrator.runMigrations());
        database = analyzer.analyze();
    }

    /*
    * see file: arte_migrations/13_program_twitter_tags.sql
     */
    @Test
    public void testTableDependencies() throws Exception {
        Table table = database.findTableByName("programs_with_twitter_tags");
        assertDependentTables(table, "programs", "broadcasts", "clusters");
    }

    private void assertDependentTables(Table table, String... dependentNames) {
        List<String> names = Arrays.asList(dependentNames);
        for (String name : names) {
            Assert.assertTrue(table.getName() + " should depend on " + name, database.findTableByName(name).getDependents().contains(table));
        }
    }
}
