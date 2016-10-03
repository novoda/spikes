package com.novoda.staticanalysis

import org.junit.Test

class JavaProjectCheckStyleTest {

    @Test
    public void shouldFailBuildWhenCheckstyleErrorEncountered() {
        String buildScript = """
            plugins {
                id 'static-analysis'
            }
            repositories {
                jcenter()
            }
            apply plugin: 'java'
            apply plugin: 'checkstyle'
            sourceSets {
                main {
                    java {
                        srcDir '${Fixtures.SOURCES_WITH_CHECKSTYLE_ERRORS}'
                    }
                }
            }
            checkstyle {
                configFile = file('${Fixtures.CHECKSTYLE_CONFIG}')
            }
        """
        TestProject.create('checkstyle/warning/java')
                .withBuildScript(buildScript)
                .buildAndFail('clean', 'check')
    }

}
