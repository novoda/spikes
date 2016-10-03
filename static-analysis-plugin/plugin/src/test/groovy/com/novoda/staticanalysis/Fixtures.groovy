package com.novoda.staticanalysis

import com.google.common.io.Resources

final class Fixtures {
    private static final File ROOT_DIR = new File(Resources.getResource('.').file).parentFile.parentFile.parentFile
    private static final File FIXTURES_DIR = new File(ROOT_DIR, 'fixtures')
    public static final File BUILD_DIR = new File(ROOT_DIR, 'build')
    public static final File CHECKSTYLE_CONFIG = fixture('checkstyle/config/modules.xml')
    public static final File SOURCES_WITH_CHECKSTYLE_ERRORS = fixture('checkstyle/errors')
    public static final File SOURCES_WITH_CHECKSTYLE_WARNINGS = fixture('checkstyle/warnings')

    private Fixtures() {
        throw new UnsupportedOperationException("No instance allowed")
    }

    public static File fixture(String path) {
        return new File(FIXTURES_DIR, path)
    }
}
