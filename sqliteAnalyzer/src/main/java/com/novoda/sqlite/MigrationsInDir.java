package com.novoda.sqlite;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

public class MigrationsInDir implements Migrations {
	private final File[] migrationFiles;
	private final Comparator<File> comparator;

	public MigrationsInDir(File directory) {
		this(directory, new NumberedFilesComparator());
	}

	public MigrationsInDir(File directory, Comparator<File> comparator) {
		this.comparator = comparator;
		migrationFiles = directory.listFiles();
	}

	@Override
	public Iterable<File> asIterable() {
		ArrayList<File> list = new ArrayList<File>(Arrays.asList(migrationFiles));
		Collections.sort(list, comparator);
		return list;
	}
}
