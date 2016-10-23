package com.novoda.staticanalysis

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

import static com.novoda.test.PenaltyExtensionSubject.assertThat

class PenaltyExtensionTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder()

    private Project project

    @Before
    public void setUp() throws Exception {
        project = ProjectBuilder.builder()
                .withProjectDir(temporaryFolder.newFolder())
                .build()
        project.apply plugin: StaticAnalysisPlugin
    }

    @Test
    public void shouldUseFailOnErrorsPenaltyThresholdsByDefault() {
        project.staticAnalysis {}

        PenaltyExtension penaltyExtension = project.staticAnalysis.penalty

        assertThat(penaltyExtension).hasMaxErrors(0)
        assertThat(penaltyExtension).hasMaxWarnings(Integer.MAX_VALUE)
    }

    @Test
    public void shouldUseCorrectThresholdsWhenPenaltyNone() {
        project.staticAnalysis {
            penalty none
        }

        PenaltyExtension penaltyExtension = project.staticAnalysis.penalty

        assertThat(penaltyExtension).hasMaxErrors(Integer.MAX_VALUE)
        assertThat(penaltyExtension).hasMaxWarnings(Integer.MAX_VALUE)
    }

    @Test
    public void shouldUseCorrectThresholdsWhenPenaltyFailOnErrors() {
        project.staticAnalysis {
            penalty failOnErrors
        }

        PenaltyExtension penaltyExtension = project.staticAnalysis.penalty

        assertThat(penaltyExtension).hasMaxErrors(0)
        assertThat(penaltyExtension).hasMaxWarnings(Integer.MAX_VALUE)
    }

    @Test
    public void shouldUseCorrectThresholdsWhenPenaltyFailOnWarnings() {
        project.staticAnalysis {
            penalty failOnWarnings
        }

        PenaltyExtension penaltyExtension = project.staticAnalysis.penalty

        assertThat(penaltyExtension).hasMaxWarnings(0)
        assertThat(penaltyExtension).hasMaxErrors(0)
    }

    @Test
    public void shouldUseCorrectThresholdsWhenCustomPenaltySpecified() {
        project.staticAnalysis {
            penalty {
                maxWarnings 100
                maxErrors 10
            }
        }

        PenaltyExtension penaltyExtension = project.staticAnalysis.penalty

        assertThat(penaltyExtension).hasMaxWarnings(100)
        assertThat(penaltyExtension).hasMaxErrors(10)
    }

}
