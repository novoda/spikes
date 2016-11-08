package com.novoda.staticanalysis.internal.pmd

import com.novoda.test.Fixtures
import org.junit.Before
import org.junit.Test

import static com.google.common.truth.Truth.assertThat
import static com.novoda.staticanalysis.internal.pmd.PmdViolationsEvaluator.PmdViolation

public class PmdViolationsEvaluatorTest {

    private PmdViolationsEvaluator evaluator

    @Before
    public void setUp() {
        evaluator = new PmdViolationsEvaluator(new XmlSlurper().parse(Fixtures.Pmd.SAMPLE_REPORT))
    }

    @Test
    public void shouldCollectDistinctViolationFromReport() {
        def expected = [new PmdViolation('1', '4', '8', '1', 'ClassWithOnlyPrivateConstructorsShouldBeFinal', 'Design', 'static-analysis-plugin/plugin/fixtures/pmd/priority1/Priority1Violator.java', '', '1'),
                        new PmdViolation('4', '6', '9', '9', 'BrokenNullCheck', 'Basic', 'static-analysis-plugin/plugin/fixtures/pmd/priority2/Priority2Violator.java', 'foo', '2')]

        Set<PmdViolation> violations = evaluator.collectViolations()

        assertThat(violations).containsExactlyElementsIn(expected)
    }

}
