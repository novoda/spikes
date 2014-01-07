package com.novoda.sqlite;

import java.io.IOException;
import java.io.Writer;

public interface Printer {
	void print(Writer writer) throws IOException;
}
