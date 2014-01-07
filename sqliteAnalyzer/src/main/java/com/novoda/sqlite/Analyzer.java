package com.novoda.sqlite;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;

import com.novoda.sqlite.model.Database;
import com.novoda.sqlite.model.Table;

public final class Analyzer {
	private Statement statement;
	private final Connection connection;

	public Analyzer(Connection connection) throws SQLException {
		this.connection = connection;
	}

	public Database analyze() throws SQLException {
		statement = connection.createStatement();
		Database database = new Database();
		readTableAndViewNames(database);
		for (Table name : database.getTables()) {
			readTableInfo(name);
		}
		statement.close();
		return database;
	}

	/**
	 * This queries the master table to get the names of all tables and views.
	 * 
	 * @param database
	 * @throws SQLException
	 */
	private void readTableAndViewNames(Database database) throws SQLException {
		ResultSet rs = statement
				.executeQuery("select * from sqlite_master where type IN ('table', 'view') AND NOT name like 'sqlite/_%' ESCAPE '/';");
		while (rs.next()) {
			database.addTable(new Table(rs.getString("name")));
			// other data in result
			// set:"type","name","tbl_name","rootpage","sql"
		}
	}

	/**
	 * Uses a PRAGMA query to derive the column infos for a given table or view.
	 * 
	 * @param table
	 * @throws SQLException
	 */
	private void readTableInfo(Table table) throws SQLException {
		ResultSet tableInfo = statement.executeQuery("PRAGMA table_info("
				+ table.getName() + ");");
		while (tableInfo.next()) {
			// System.out.println(tableInfo.getString("name") + ", "
			// + tableInfo.getString("type"));
			table.addColumn(tableInfo.getString("name"));
		}
	}

	/**
	 * We can also read the column info using a Limit-0 select query on a table.
	 * 
	 * @throws SQLException
	 */
	private void readLimitZeroQueryResultMetaData() throws SQLException {
		String sql = "select * from BROADCASTS LIMIT 0";
		ResultSet rs = statement.executeQuery(sql);
		ResultSetMetaData mrs = rs.getMetaData();
		for (int i = 1; i <= mrs.getColumnCount(); i++) {
			Object[] row = new Object[4];
			row[0] = mrs.getColumnLabel(i);
			row[1] = mrs.getColumnTypeName(i);
			row[2] = mrs.getPrecision(i);
			row[3] = mrs.getTableName(i);
			System.out.println(Arrays.asList(row));
		}

	}

}