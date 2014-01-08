package com.novoda.sqlite;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.EnumSet;

import javax.lang.model.element.Modifier;

import com.squareup.javawriter.JavaWriter;

public final class Generator {

	private final Printer[] printers;
	private final String packageName;
	private final File targetDir;

	public Generator(File targetDir, String packageName, Printer... printers) {
		this.targetDir = targetDir;
		this.packageName = packageName;
		this.printers = printers;
	}

	public void print() throws IOException {
		File targetFile = new File(targetDir, "DB.java");
		PrintWriter printer = new PrintWriter(targetFile);
		JavaWriter javaWriter = new JavaWriter(printer);
		emitClass(javaWriter);
		printer.flush();
		printer.close();
	}

	private void emitClass(JavaWriter javaWriter) throws IOException {
		javaWriter.emitPackage(packageName).beginType("DB", "class", EnumSet.of(Modifier.PUBLIC, Modifier.FINAL));
		for (Printer printer : printers) {
			printer.print(javaWriter);
		}
		javaWriter.endType();
	}

}
