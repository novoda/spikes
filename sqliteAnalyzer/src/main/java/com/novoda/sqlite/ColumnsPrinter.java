package com.novoda.sqlite;

import java.io.IOException;
import java.util.EnumSet;

import javax.lang.model.element.Modifier;

import com.novoda.sqlite.model.Column;
import com.novoda.sqlite.model.Database;
import com.novoda.sqlite.model.Table;
import com.squareup.javawriter.JavaWriter;

public final class ColumnsPrinter implements Printer {
	private final Database database;

	public ColumnsPrinter(Database database) {
		this.database = database;
	}

	/**
	 * Prints a class containing enum classes for the column names of each
	 * table.
	 * 
	 * @param writer
	 * @throws IOException
	 */
	@Override
	public void print(JavaWriter writer) throws IOException {
		emitClass(writer);

	}

	private void emitClass(JavaWriter javaWriter) throws IOException {
		javaWriter.beginType("Columns", "class", EnumSet.of(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL));
		for (Table table : database.getTables()) {
			emitTable(table, javaWriter);
		}
		javaWriter.endType();
	}

	private void emitTable(Table table, JavaWriter javaWriter) throws IOException {
		javaWriter.beginType(table.getName(), "enum", EnumSet.of(Modifier.PUBLIC));
		for (Column column : table.getColumns()) {
			javaWriter.emitEnumValue(column.getName());
		}
		javaWriter.endType();
	}
}
