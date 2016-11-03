package com.novoda.staticanalysis

class PenaltyExtension {
    private int maxWarnings = Integer.MAX_VALUE
    private int maxErrors = 0

    void maxErrors(int value) {
        maxErrors = Math.max(0, value)
    }

    void maxWarnings(int value) {
        maxWarnings = Math.max(0, value)
    }

    int getMaxErrors() {
        maxErrors
    }

    int getMaxWarnings() {
        maxWarnings
    }

}
