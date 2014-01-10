package com.novoda.sqlite.model;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Table {
	private final String name;
	private final List<String> columns = new ArrayList<String>();
	private final String sql;

	public Table(String name, String sql) {
		this.name = name;
		this.sql = sql;
	}

	public void addColumn(String column) {
		columns.add(column);
	}

	public String getName() {
		return name;
	}
	
	public String getSql() {
		return sql;
	}

	public Collection<String> getColumns() {
		return Collections.unmodifiableCollection(columns);
	}
}
