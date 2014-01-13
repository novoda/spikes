package com.novoda.sqlite.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TableCreateStatementParser {
    private final static Pattern p = Pattern.compile("(JOIN|FROM)\\W+(\\w+)");

    public List<String> parseUsedTables(String sqlCreateStatement) {
        ArrayList<String> baseTables = new ArrayList<String>();
        Matcher m = p.matcher(sqlCreateStatement.toUpperCase());
        while (m.find())
            baseTables.add(m.group(2));
        return baseTables;
    }
}
