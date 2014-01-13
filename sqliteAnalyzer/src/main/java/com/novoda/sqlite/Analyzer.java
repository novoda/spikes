package com.novoda.sqlite;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;

import com.novoda.sqlite.impl.TableCreateStatementParser;
import com.novoda.sqlite.model.Column;
import com.novoda.sqlite.model.DataAffinity;
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
        addTableDependencies(database);
        statement.close();
        return database;
    }

    private void addTableDependencies(Database database) {
        TableCreateStatementParser parser = new TableCreateStatementParser();
        for (Table table : database.getTables()) {
            List<String> dependsOnTables = parser.parseUsedTables(table.getSql());
            for (String dependsOnTable : dependsOnTables) {
                Table baseTable = database.findTableByName(dependsOnTable);
                baseTable.addDependent(table);
            }
        }
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
            database.addTable(new Table(rs.getString("name"), rs.getString("sql")));
        }
    }

    /**
     * Uses a PRAGMA query to derive the column infos for a given table or view.
     *
     * @param table
     * @throws SQLException
     */
    private void readTableInfo(Table table) throws SQLException {
        ResultSet tableInfo = statement.executeQuery("PRAGMA table_info(" + table.getName() + ");");
        while (tableInfo.next()) {
            table.addColumn(createColumn(tableInfo));
        }
    }

    private Column createColumn(ResultSet resultSet) throws SQLException {
        String name = resultSet.getString("name");
        String type = resultSet.getString("type");
        boolean nullable = !resultSet.getBoolean("notNull");
        return new Column(name, type, nullable, computeAffinity(type));
    }

    /*
     * See http://www.sqlite.org/datatype3.html
     * section 2.1 Determination of column affinity
     */
    private DataAffinity computeAffinity(String type) {
        String deftype = type.toLowerCase();
        if (deftype.contains("int"))
            return DataAffinity.INTEGER;
        if (containsOneOf(deftype, "char", "clob", "text"))
            return DataAffinity.TEXT;
        if (containsOneOf(deftype, "", "blob"))
            return DataAffinity.NONE;
        if (containsOneOf(deftype, "real", "floa", "doub"))
            return DataAffinity.REAL;
        return DataAffinity.NUMERIC;
    }

    private boolean containsOneOf(String toCheck, String... values) {
        for (String value : values) {
            if (toCheck.contains(value))
                return true;
        }
        return false;
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