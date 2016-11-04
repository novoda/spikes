package com.novoda.staticanalysis

import com.novoda.test.Fixtures
import org.junit.Before
import org.junit.Test

import static com.google.common.truth.Truth.assertThat
import static com.novoda.staticanalysis.PmdViolationsEvaluator.PmdViolation

public class PmdViolationsEvaluatorTest {

    private PmdViolationsEvaluator evaluator

    @Before
    public void setUp() {
        evaluator = new PmdViolationsEvaluator(new XmlSlurper().parse(Fixtures.Pmd.SAMPLE_REPORT))
    }

    @Test
    public void shouldCollectDistinctViolationFromReport() {
        def expected = [new PmdViolation('1', '4', '8', '1', 'ClassWithOnlyPrivateConstructorsShouldBeFinal', 'Design', '/Users/toto/novoda/spikes/static-analysis-plugin/plugin/fixtures/pmd/priority1/Priority1Violator.java', '', '1'),
                        new PmdViolation('4', '6', '9', '9', 'BrokenNullCheck', 'Basic', '/Users/toto/novoda/spikes/static-analysis-plugin/plugin/fixtures/pmd/priority2/Priority2Violator.java', 'foo', '2')]

        Set<PmdViolation> violations = evaluator.collectViolations()

        assertThat(violations).containsExactlyElementsIn(expected)
    }

}
