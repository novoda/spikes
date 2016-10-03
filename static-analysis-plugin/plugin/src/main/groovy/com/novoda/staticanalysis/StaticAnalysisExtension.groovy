package com.novoda.staticanalysis

class StaticAnalysisExtension {

    final Penalty none = Penalty.NONE
    final Penalty failOnErrors = Penalty.FAIL_ON_ERRORS
    final Penalty failOnWarnings = Penalty.FAIL_ON_WARNINGS

    Penalty penalty = failOnErrors

}
