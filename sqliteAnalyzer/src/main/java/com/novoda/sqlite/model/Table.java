package com.novoda.sqlite.model;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Table {
	private final String name;
	private final List<String> columns = new ArrayList<String>();

	public Table(String name) {
		this.name = name;
	}

	public void addColumn(String column) {
		columns.add(column);
	}

	public String getName() {
		return name;
	}

	public Collection<String> getColumns() {
		return Collections.unmodifiableCollection(columns);
	}
}
