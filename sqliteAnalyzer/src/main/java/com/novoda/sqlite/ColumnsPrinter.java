package com.novoda.sqlite;

import java.io.IOException;
import java.io.Writer;
import java.util.EnumSet;

import javax.lang.model.element.Modifier;

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
	public void print(Writer writer) throws IOException {
		JavaWriter javaWriter = new JavaWriter(writer);
		emitClass(javaWriter);

	}

	private void emitClass(JavaWriter javaWriter) throws IOException {
		javaWriter.emitPackage("com.example").beginType("Columns", "class",
				EnumSet.of(Modifier.PUBLIC, Modifier.FINAL));
		for (Table table : database.getTables()) {
			emitTable(table, javaWriter);
		}
		javaWriter.endType();
	}

	private void emitTable(Table table, JavaWriter javaWriter)
			throws IOException {
		javaWriter.beginType(table.getName(), "enum",
				EnumSet.of(Modifier.PUBLIC));
		for (String column : table.getColumns()) {
			javaWriter.emitEnumValue(column);
		}
		javaWriter.endType();
	}
}
