package com.novoda.sqlite;

import com.novoda.sqlite.model.Database;
import com.novoda.sqlite.model.Table;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class AnalyzerTest {

    private Analyzer analyzer;

    @Before
    public void setUp() throws Exception {
        String migrationsDir = MigrationsInDir.class.getResource("/migrations").getFile();
        MigrationsInDir migrations = new MigrationsInDir(new File(migrationsDir));
        Migrator migrator = new Migrator(migrations);
        analyzer = new Analyzer(migrator.runMigrations());
    }

    @Test
    public void shouldAnalyzeDbFromSampleMigrations() throws Exception {
        Database database = analyzer.analyze();
        assertTableNames(database, "people", "numbers");
        assertTableColumns(database, "people", new String[]{"name", "age", "firstname"});
    }

    private void assertTableColumns(Database database, String tableName, String[] columnNames) {
        assertColumns(database.findTableByName(tableName), columnNames);
    }

    private void assertColumns(Table table, String[] columnNames) {
        for (String columnName : columnNames) {
            assertColumn(table, columnName);
        }
        assertEquals(table.getColumns().size(), columnNames.length);
    }

    private void assertColumn(Table table, String columnName) {
        assertTrue("no column '" + columnName + "' in table '" + table.getName() + "'", table.findColumnByName(columnName) != null);
    }

    private void assertTableNames(Database database, String... names) {
        for (String name : names) {
            assertTableName(database, name);
        }
        assertEquals(database.getTables().size(), names.length);
    }

    private void assertTableName(Database database, String name) {
        for (Table table : database.getTables()) {
            if (table.getName().equals(name)) {
                return;
            }
        }
        fail("no table with name '" + name + "' found");
    }
}
