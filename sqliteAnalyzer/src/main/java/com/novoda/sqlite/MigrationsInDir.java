package com.novoda.sqlite;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

public class MigrationsInDir implements Migrations {
	private final File[] migrationFiles;
	private final Comparator<File> comparator;

	public MigrationsInDir(String directory) {
		this(directory, new NumberedFilesComparator());
	}

	public MigrationsInDir(String directory, Comparator<File> comparator) {
		this.comparator = comparator;
		migrationFiles = new File(directory).listFiles();
	}

	@Override
	public Iterable<File> asIterable() {
		ArrayList<File> list = new ArrayList<File>(Arrays.asList(migrationFiles));
		Collections.sort(list, comparator);
		return list;
	}
}
