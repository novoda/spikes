package com.novoda.test

import com.google.common.io.Resources

public final class Fixtures {
    private static final Exception NO_INSTANCE_ALLOWED = new UnsupportedOperationException("No instance allowed");
    private static final File ROOT_DIR = new File(Resources.getResource('.').file).parentFile.parentFile.parentFile
    private static final File FIXTURES_DIR = new File(ROOT_DIR, 'fixtures')
    public static final File BUILD_DIR = new File(ROOT_DIR, 'build')
    public static final File LOCAL_PROPERTIES = new File(ROOT_DIR.parentFile, 'local.properties')
    public static final File ANDROID_MANIFEST = new File(FIXTURES_DIR, 'AndroidManifest.xml')

    private Fixtures() {
        throw NO_INSTANCE_ALLOWED
    }

    public final static class Checkstyle {
        public static final File MODULES = new File(FIXTURES_DIR, 'checkstyle/config/modules.xml')
        public static final File SOURCES_WITH_ERRORS = new File(FIXTURES_DIR, 'checkstyle/errors')
        public static final File SOURCES_WITH_WARNINGS = new File(FIXTURES_DIR, 'checkstyle/warnings')

        private Checkstyle() {
            throw NO_INSTANCE_ALLOWED
        }
    }

    public final static class Pmd {
        public static final File RULES = new File(FIXTURES_DIR, 'pmd/config/rules.xml')
        public static final File SOURCES_WITH_PRIORITY_1_VIOLATION = new File(FIXTURES_DIR, 'pmd/priority1')
        public static final File SOURCES_WITH_PRIORITY_2_VIOLATION = new File(FIXTURES_DIR, 'pmd/priority2')
        public static final File SOURCES_WITH_PRIORITY_3_VIOLATION = new File(FIXTURES_DIR, 'pmd/priority3')
        public static final File SOURCES_WITH_PRIORITY_4_VIOLATION = new File(FIXTURES_DIR, 'pmd/priority4')
        public static final File SOURCES_WITH_PRIORITY_5_VIOLATION = new File(FIXTURES_DIR, 'pmd/priority5')
        public static final File SAMPLE_REPORT = new File(FIXTURES_DIR, 'pmd/reports/sample.xml')
    }

}
