package com.novoda.sqlite;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import com.novoda.sqlite.model.Database;

public class Main {

	public static void main(String[] args) throws ClassNotFoundException,
			SQLException, IOException {
		// load the sqlite-JDBC driver using the current class loader
		Class.forName("org.sqlite.JDBC");

		Connection connection = null;
		try {
			connection = DriverManager.getConnection("jdbc:sqlite::memory:");
			executeMigrations(connection);
			Database database = new Analyzer(connection).analyze();
			print(new ColumnsPrinter(database), "Columns.java");
			print(new TablesPrinter(database), "Tables.java");
		} finally {
			if (connection != null)
				connection.close();
		}
	}

	private static void print(Printer printer, String file) throws IOException {
		String genDir = "src/main/gen/com/example/";
		new File(genDir).mkdirs();
		File targetFile = new File(genDir + file);
		PrintWriter writer = new PrintWriter(
				targetFile);
		printer.print(writer);
		writer.flush();
		writer.close();
	}

	private static void executeMigrations(Connection connection)
			throws IOException, SQLException {
		Statement statement = connection.createStatement();
		for (File migration : getMigrationFiles()) {
			List<String> statements = SQLFile.statementsFrom(migration);
			for (String sqlCommand : statements) {
				statement.executeUpdate(sqlCommand);
			}
		}
		statement.close();
	}

	private static File[] getMigrationFiles() {
		File[] migrations = new File("migrations").listFiles();
		Arrays.sort(migrations, new Comparator<File>() {
			@Override
			public int compare(File o1, File o2) {
				return getLeadingNumber(o1.getName())
						- getLeadingNumber(o2.getName());
			}

			private int getLeadingNumber(String name) {
				return Integer.valueOf(name.split("_")[0]);
			}
		});
		return migrations;
	}

}
