package com.novoda.sqlite.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public final class Database {
	private final List<Table> tables = new ArrayList<Table>();

	public void addTable(Table table) {
		tables.add(table);
	}

	public Collection<Table> getTables() {
		return Collections.unmodifiableCollection(tables);
	}

    public Table findTableByName(String tableName) {
        for (Table table : tables)
            if (table.getName().equalsIgnoreCase(tableName)) {
                return table;
            }
        return null;
    }

}
