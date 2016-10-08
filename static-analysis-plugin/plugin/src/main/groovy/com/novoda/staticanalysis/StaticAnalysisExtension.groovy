package com.novoda.staticanalysis

class StaticAnalysisExtension {
    final Closure<Penalty> none = { Penalty.NONE}
    final Closure<Penalty> failOnErrors = { Penalty.FAIL_ON_ERRORS }
    final Closure<Penalty> failOnWarnings = { Penalty.FAIL_ON_WARNINGS }

    private Closure<Penalty> currentPenalty = failOnErrors

    Penalty getPenalty() {
        currentPenalty.call()
    }

    void penalty(Closure<Penalty> penalty) {
        currentPenalty = penalty
    }
}
