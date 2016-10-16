package com.novoda.staticanalysis

class PenaltyExtension {
    private int maxWarnings = Integer.MAX_VALUE
    private int maxErrors = 0

    void maxErrors(int value) {
        maxErrors = value
    }

    void maxWarnings(int value) {
        maxWarnings = value
    }

    int getMaxErrors() {
        maxErrors
    }

    int getMaxWarnings() {
        maxWarnings
    }

}
