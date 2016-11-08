package com.novoda.staticanalysis

import org.gradle.api.Action

class StaticAnalysisExtension {

    final Action<? super PenaltyExtension> none = {
        it.maxWarnings = Integer.MAX_VALUE
        it.maxErrors = Integer.MAX_VALUE
    }

    final Action<? super PenaltyExtension> failOnErrors = {
        it.maxWarnings = Integer.MAX_VALUE
        it.maxErrors = 0
    }

    final Action<? super PenaltyExtension> failOnWarnings = {
        it.maxWarnings = 0
        it.maxErrors = 0
    }

    private PenaltyExtension currentPenalty = new PenaltyExtension()

    void penalty(Action<? super PenaltyExtension> action) {
        action.execute(currentPenalty)
    }

    PenaltyExtension getPenalty() {
        currentPenalty
    }

}
