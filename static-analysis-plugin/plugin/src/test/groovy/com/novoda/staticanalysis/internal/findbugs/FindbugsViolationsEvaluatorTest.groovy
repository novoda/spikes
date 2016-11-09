package com.novoda.staticanalysis.internal.findbugs

import com.novoda.test.Fixtures
import org.junit.Before
import org.junit.Test

import static com.google.common.truth.Truth.assertThat

class FindbugsViolationsEvaluatorTest {
    private FinbugsViolationsEvaluator evaluator

    @Before
    public void setUp() {
        evaluator = new FinbugsViolationsEvaluator(Fixtures.Findbugs.SAMPLE_REPORT)
    }

    @Test
    public void shouldCountAllWarningsWithPriority1AsErrors() {
        int count = evaluator.errorsCount()

        assertThat(count).isEqualTo(1);
    }

    @Test
    public void shouldCountAllWarningsWithPriority2And3AsWarnings() {
        int count = evaluator.warningsCount()

        assertThat(count).isEqualTo(2);
    }
}
