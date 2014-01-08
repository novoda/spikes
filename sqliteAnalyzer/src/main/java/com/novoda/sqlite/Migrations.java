package com.novoda.sqlite;

import java.io.File;

public interface Migrations {

	public abstract Iterable<File> asIterable();

}