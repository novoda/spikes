package com.novoda.test

class TestAndroidProject extends TestProject {

    TestAndroidProject() {
        super(BuildScriptBuilder.forAndroid())
        copyFile(Fixtures.LOCAL_PROPERTIES, 'local.properties')
    }

    @Override
    List<String> defaultArguments() {
        ['-x', 'lint'] + super.defaultArguments()
    }
}
