package com.novoda.sqlite;

import com.novoda.sqlite.impl.SQLFile;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class Migrator {

	private final Iterable<File> migrations;

	public Migrator(Migrations migrations) {
		this.migrations = migrations.asIterable();
	}

	public Connection runMigrations() throws IOException, SQLException {
		Connection connection = DriverManager.getConnection("jdbc:sqlite::memory:");
		executeMigrations(connection);
		return connection;
	}

	private void executeMigrations(Connection connection) throws IOException, SQLException {
		Statement statement = connection.createStatement();
		for (File migration : migrations) {
			List<String> statements = SQLFile.statementsFrom(migration);
			for (String sqlCommand : statements) {
				statement.executeUpdate(sqlCommand);
			}
		}
		statement.close();
	}

}
