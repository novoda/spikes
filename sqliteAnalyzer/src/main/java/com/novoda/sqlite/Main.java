package com.novoda.sqlite;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import com.novoda.sqlite.model.Database;

public class Main {

	public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException {
		// load the sqlite-JDBC driver using the current class loader
		Class.forName("org.sqlite.JDBC");

		Connection connection = null;
		try {
			connection = new Migrator(new MigrationsInDir(new File("arte_migrations"))).runMigrations();
			Database database = new Analyzer(connection).analyze();
			generateJavaCode(database);
		} finally {
			if (connection != null)
				connection.close();
		}
	}

	private static void generateJavaCode(Database database) throws IOException {
		File targetDir = new File("src/main/gen/");
        targetDir.mkdirs();
		Generator generator = new Generator(targetDir, "com.example", new ColumnsPrinter(database), new TablesPrinter(database));
		generator.print();
	}
}
