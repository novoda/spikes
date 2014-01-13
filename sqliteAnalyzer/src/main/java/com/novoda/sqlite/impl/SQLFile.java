package com.novoda.sqlite.impl;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Parsing .sql files and get single statements suitable for insertion.
 */
public class SQLFile {

	private static final String STATEMENT_END_CHARACTER = ";";
	private static final String LINE_COMMENT_START_CHARACTERS = "--";
	private static final String BLOCK_COMMENT_START_CHARACTERS = "/*";
	private static final String BLOCK_COMMENT_END_CHARACTERS = "*/";
	private static final String LINE_CONCATENATION_CHARACTER = " ";

	private List<String> statements;

	private boolean inComment = false;

	public void parse(Reader in) throws IOException {
		BufferedReader reader = new BufferedReader(in);
		statements = new ArrayList<String>();
		String line;
		StringBuilder statement = new StringBuilder();
		while ((line = reader.readLine()) != null) {
			line = stripOffTrailingComment(line).trim();

			if (line.length() == 0) {
				continue;
			}

			if (line.startsWith(BLOCK_COMMENT_START_CHARACTERS)) {
				inComment = true;
				continue;
			}

			if (inComment && line.endsWith(BLOCK_COMMENT_END_CHARACTERS)) {
				inComment = false;
				continue;
			}

			if (inComment) {
				continue;
			}

			statement.append(line);
			if (!line.endsWith(STATEMENT_END_CHARACTER)) {
				statement.append(LINE_CONCATENATION_CHARACTER);
				continue;
			}

			statements.add(statement.toString());
			statement.setLength(0);
		}
		reader.close();
		if (statement.length() > 0) {
			throw new IOException(
					"incomplete sql statement (missing semicolon?): "
							+ statement.toString());
		}
	}

	private String stripOffTrailingComment(String line) {
		int commentStartIndex = line.indexOf(LINE_COMMENT_START_CHARACTERS);
		if (commentStartIndex != -1) {
			return line.substring(0, commentStartIndex);
		}
		return line;
	}

	public List<String> getStatements() {
		return statements;
	}

	public static List<String> statementsFrom(Reader reader) throws IOException {
		SQLFile file = new SQLFile();
		file.parse(reader);
		return file.getStatements();
	}

	public static List<String> statementsFrom(File sqlfile) throws IOException {
		FileReader reader = null;
		try {
			reader = new FileReader(sqlfile);
			SQLFile file = new SQLFile();
			file.parse(reader);
			return file.getStatements();
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
	}
}
