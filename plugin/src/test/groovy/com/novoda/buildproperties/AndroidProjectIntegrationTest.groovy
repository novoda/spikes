package com.novoda.buildproperties

import com.google.common.io.Resources
import com.novoda.buildproperties.test.EntrySubject
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.internal.DefaultGradleRunner
import org.junit.ClassRule
import org.junit.Test
import org.junit.contrib.java.lang.system.EnvironmentVariables
import org.junit.rules.RuleChain
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

import static com.google.common.truth.Truth.assertThat

public class AndroidProjectIntegrationTest {

    public static final ProjectRule PROJECT = new ProjectRule()
    public static final EnvironmentVariables ENV = new EnvironmentVariables()
    public static final String COMMAND_LINE_PROPERTY = 'modified from command line'
    public static final String ENV_VAR_VALUE = '123456'

    @ClassRule
    public static TestRule classRule() {
        RuleChain.outerRule(PROJECT).around(ENV)
    }

    @Test
    public void shouldGenerateTypedFieldsFromTypedValuesProvidedInDefaultBuildConfig() {
        [PROJECT.debugBuildConfig.text, PROJECT.releaseBuildConfig.text].each { String generatedBuildConfig ->
            assertThat(generatedBuildConfig).contains('public static final boolean TEST_BOOLEAN = false;')
            assertThat(generatedBuildConfig).contains('public static final double TEST_DOUBLE = 3.141592653589793;')
            assertThat(generatedBuildConfig).contains('public static final int TEST_INT = 42;')
            assertThat(generatedBuildConfig).contains('public static final long TEST_LONG = 9223372036854775807L;')
            assertThat(generatedBuildConfig).contains('public static final String TEST_STRING = "whateva";')
        }
    }

    @Test
    public void shouldGenerateStringFieldsFromPropertiesFileProvidedInDefaultBuildConfig() {
        [PROJECT.debugBuildConfig.text, PROJECT.releaseBuildConfig.text].each { String generatedBuildConfig ->
            assertThat(generatedBuildConfig).contains("public static final String GOOGLE_MAPS_KEY = \"${PROJECT.secrets['googleMapsKey'].string}\";")
            assertThat(generatedBuildConfig).contains("public static final String SUPER_SECRET = \"${PROJECT.secrets['superSecret'].string}\";")
        }
    }

    @Test
    public void shouldGenerateStringFieldForSinglePropertyProvidedInBuildTypeBuildConfig() {
        assertThat(PROJECT.debugBuildConfig.text).contains("public static final String ONLY_DEBUG")
        assertThat(PROJECT.releaseBuildConfig.text).doesNotContain("public static final String ONLY_DEBUG")
    }

    @Test
    public void shouldGenerateXmlResourcesProvidedInBuildTypeResValues() {
        String debugGeneratedResources = PROJECT.debugResValues.text
        assertThat(debugGeneratedResources).contains('<bool name="debug_test_bool">true</bool>')
        assertThat(debugGeneratedResources).contains('<integer name="debug_test_int">100</integer>')
        assertThat(debugGeneratedResources).contains('<string name="debug_test_string" translatable="false">"dunno bro..."</string>')
        assertThat(debugGeneratedResources).contains('<string name="google_maps_key" translatable="false">"AIza???????????????????????????????????"</string>')
        assertThat(debugGeneratedResources).contains('<string name="super_secret" translatable="false">"classified"</string>')
        assertThat(debugGeneratedResources).contains('<string name="another_key" translatable="false">"another???????????????????????????????????"</string>')
    }

    @Test
    public void shouldNotGenerateXmlResourcesWhenNotProvidedInBuildTypeResValues() {
        assertThat(PROJECT.releaseResValues.exists()).isFalse()
    }

    @Test
    public void shouldOverridePropertyValueInFileWithValueProvidedViaCommandLine() {
        assertThat(PROJECT.secrets['overridable']).isNotEqualTo(COMMAND_LINE_PROPERTY)
        [PROJECT.debugBuildConfig.text, PROJECT.releaseBuildConfig.text].each { String generatedBuildConfig ->
            assertThat(generatedBuildConfig).contains("public static final String OVERRIDABLE = \"$COMMAND_LINE_PROPERTY\";")
        }
    }

    @Test
    public void shouldEvaluateFallbackWhenNeeded() {
        EntrySubject.assertThat(PROJECT.secrets['FOO']).willThrow(IllegalArgumentException)
        [PROJECT.debugBuildConfig.text, PROJECT.releaseBuildConfig.text].each { String generatedBuildConfig ->
            assertThat(generatedBuildConfig).contains('public static final String FOO = "bar";')
        }
    }

    @Test
    public void shouldEvaluateEnvironmentVariable() {
        assertThat(System.getenv('WAT')).isEqualTo(ENV_VAR_VALUE)
        [PROJECT.debugBuildConfig.text, PROJECT.releaseBuildConfig.text].each { String generatedBuildConfig ->
            assertThat(generatedBuildConfig).contains("public static final String WAT = \"$ENV_VAR_VALUE\";")
        }
    }

    @Test
    public void shouldSignReleaseBuildUsingProperties() throws Exception {
        assertThat(new File(PROJECT.apkDir, 'app-release.apk').exists()).isTrue()
    }

    static class ProjectRule implements TestRule {
        File projectDir
        BuildResult buildResult
        File debugBuildConfig
        File releaseBuildConfig
        File debugResValues
        File releaseResValues
        File apkDir
        Entries secrets

        @Override
        Statement apply(Statement base, Description description) {
            ENV.set('WAT', ENV_VAR_VALUE)

            File propertiesFile = new File(Resources.getResource('any.properties').toURI())
            File rootDir = propertiesFile.parentFile.parentFile.parentFile.parentFile.parentFile
            projectDir = new File(rootDir, 'sample')
            File buildDir = new File(projectDir, 'app/build')
            apkDir = new File(buildDir, 'outputs/apk')
            buildResult = DefaultGradleRunner.create()
                    .withProjectDir(projectDir)
                    .withDebug(true)
                    .forwardStdOutput(new OutputStreamWriter(System.out))
                    .withArguments('clean', "-Poverridable=$COMMAND_LINE_PROPERTY", 'assemble')
                    .build()
            debugBuildConfig = new File(buildDir, 'generated/source/buildConfig/debug/com/novoda/buildpropertiesplugin/sample/BuildConfig.java')
            releaseBuildConfig = new File(buildDir, 'generated/source/buildConfig/release/com/novoda/buildpropertiesplugin/sample/BuildConfig.java')
            debugResValues = new File(buildDir, 'generated/res/resValues/debug/values/generated.xml')
            releaseResValues = new File(buildDir, 'generated/res/resValues/release/values/generated.xml')
            secrets = FilePropertiesEntries.create(new File(projectDir, 'properties/secrets.properties'))
            return base;
        }
    }

}
