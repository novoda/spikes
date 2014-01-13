package com.novoda.sqlite.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public final class Table {
    private final String name;
    private final List<Column> columns = new ArrayList<Column>();
    private final String sql;
    private final List<Table> dependents = new ArrayList<Table>();

    public Table(String name, String sql) {
        this.name = name;
        this.sql = sql;
    }

    public void addColumn(Column column) {
        columns.add(column);
    }

    public Column findColumnByName(String name) {
        for (Column column : columns) {
            if (column.getName().equalsIgnoreCase(name))
                return column;
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public String getSql() {
        return sql;
    }

    public Collection<Column> getColumns() {
        return Collections.unmodifiableCollection(columns);
    }

    public void addDependent(Table table) {
        dependents.add(table);
    }

    public Collection<Table> getDependents() {
        return Collections.unmodifiableCollection(dependents);
    }
}
