package com.novoda.staticanalysis

import org.junit.Before
import org.junit.Test

import static com.google.common.truth.Truth.assertThat
import static com.novoda.staticanalysis.PmdViolationsEvaluator.PmdViolation

public class PmdViolationsEvaluatorTest {

    String xml = '''<?xml version="1.0" encoding="UTF-8"?>
<pmd version="5.5.1" timestamp="2016-11-02T17:37:41.144">
<file name="/Users/toto/novoda/spikes/static-analysis-plugin/plugin/fixtures/pmd/priority1/Priority1Violator.java">
<violation beginline="1" endline="4" begincolumn="8" endcolumn="1" rule="ClassWithOnlyPrivateConstructorsShouldBeFinal" ruleset="Design" externalInfoUrl="https://pmd.github.io/pmd-5.5.1/pmd-java/rules/java/design.html#ClassWithOnlyPrivateConstructorsShouldBeFinal" priority="1">
A class which only has private constructors should be final
</violation>
</file>
<file name="/Users/toto/novoda/spikes/static-analysis-plugin/plugin/fixtures/pmd/priority2/Priority2Violator.java">
<violation beginline="4" endline="6" begincolumn="9" endcolumn="9" rule="BrokenNullCheck" ruleset="Basic" class="Priority2Violator" method="foo" externalInfoUrl="https://pmd.github.io/pmd-5.5.1/pmd-java/rules/java/basic.html#BrokenNullCheck" priority="2">
Method call on object which may be null
</violation>
<violation beginline="4" endline="6" begincolumn="9" endcolumn="9" rule="BrokenNullCheck" ruleset="Basic" class="Priority2Violator" method="foo" externalInfoUrl="https://pmd.github.io/pmd-5.5.1/pmd-java/rules/java/basic.html#BrokenNullCheck" priority="2">
Method call on object which may be null
</violation>
</file>
</pmd>'''
    private PmdViolationsEvaluator evaluator

    @Before
    public void setUp() {
        evaluator = new PmdViolationsEvaluator(new XmlSlurper().parseText(xml))
    }

    @Test
    public void shouldCollectDistinctViolationFromReport() {
        def expected = [new PmdViolation('1', '4', '8', '1', 'ClassWithOnlyPrivateConstructorsShouldBeFinal', 'Design', '/Users/toto/novoda/spikes/static-analysis-plugin/plugin/fixtures/pmd/priority1/Priority1Violator.java', '', '1'),
                        new PmdViolation('4', '6', '9', '9', 'BrokenNullCheck', 'Basic', '/Users/toto/novoda/spikes/static-analysis-plugin/plugin/fixtures/pmd/priority2/Priority2Violator.java', 'foo', '2')]

        Set<PmdViolation> violations = evaluator.collectViolations()

        assertThat(violations).containsExactlyElementsIn(expected)
    }

}
