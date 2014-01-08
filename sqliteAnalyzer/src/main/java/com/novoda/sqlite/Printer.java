package com.novoda.sqlite;

import java.io.IOException;

import com.squareup.javawriter.JavaWriter;

public interface Printer {
	void print(JavaWriter writer) throws IOException;
}
