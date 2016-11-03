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


}
