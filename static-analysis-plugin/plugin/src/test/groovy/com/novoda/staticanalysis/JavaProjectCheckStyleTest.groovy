package com.novoda.staticanalysis

import org.junit.Rule
import org.junit.Test

class JavaProjectCheckStyleTest {

    @Rule
    public TestProjectRule project = new TestProjectRule()

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
        project.withBuildScript(buildScript)
                .buildAndFail('clean', 'check')
    }

    @Test
    public void shouldNotFailBuildWhenCheckstyleErrorEncountered() {
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
                        srcDir '${Fixtures.SOURCES_WITH_CHECKSTYLE_WARNINGS}'
                    }
                }
            }
            checkstyle {
                configFile = file('${Fixtures.CHECKSTYLE_CONFIG}')
            }
        """
        project.withBuildScript(buildScript)
                .build('clean', 'check')
    }

}
