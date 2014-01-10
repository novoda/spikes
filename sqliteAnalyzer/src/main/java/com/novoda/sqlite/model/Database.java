package com.novoda.sqlite.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Database {
	private final List<Table> tables = new ArrayList<Table>();

	public void addTable(Table table) {
		tables.add(table);
	}

	public Collection<Table> getTables() {
		return Collections.unmodifiableCollection(tables);
	}
}
