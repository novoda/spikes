package com.novoda.staticanalysis.internal.findbugs

import groovy.util.slurpersupport.GPathResult

class FinbugsViolationsEvaluator {

    private final GPathResult xml

    FinbugsViolationsEvaluator(File report) {
        this(new XmlSlurper().parse(report))
    }

    FinbugsViolationsEvaluator(GPathResult xml) {
        this.xml = xml
    }

    int errorsCount() {
        count('@priority_1')
    }

    private int count(String attr) {
        def count = xml.FindBugsSummary[attr]
        count == "" ? 0 : count.toInteger()
    }

    int warningsCount() {
        count("@priority_2") + count("@priority_3")
    }

}
