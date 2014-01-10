package com.novoda.sqlite;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;

import javax.lang.model.element.Modifier;

import com.novoda.sqlite.model.Database;
import com.novoda.sqlite.model.Table;
import com.squareup.javawriter.JavaWriter;

public final class TablesPrinter implements Printer {
	private final Database database;

	public TablesPrinter(Database database) {
		this.database = database;
	}

	/**
	 * Prints an enum containing the names of each table.
	 * 
	 * @param writer
	 * @throws IOException
	 */
	@Override
	public void print(JavaWriter writer) throws IOException {
		emitClass(writer);

	}

	private void emitClass(JavaWriter javaWriter) throws IOException {
		javaWriter.beginType("Tables", "enum",
				EnumSet.of(Modifier.PUBLIC));
		for (Table table : lexSortedTables()) {
			javaWriter.emitEnumValue(table.getName());
		}
		javaWriter.endType();
	}

	private List<Table> lexSortedTables() {
		ArrayList<Table> copy = new ArrayList<Table>(database.getTables());
		Collections.sort(copy, new Comparator<Table>() {
			@Override
			public int compare(Table left, Table right) {
				return left.getName().compareTo(right.getName());
			}
		} );
		return copy;
	}
}
