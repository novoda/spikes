package com.novoda.staticanalysis.internal;

class Violations {
    private final String toolName
    private int errors = 0
    private int warnings = 0
    private List<String> reports = []

    Violations(String toolName) {
        this.toolName = toolName
    }

    public String getName() {
        return toolName
    }

    public int getErrors() {
        errors
    }

    public int getWarnings() {
        warnings
    }

    public void addViolations(int errors, int warnings, String reportUrl) {
        this.errors += errors
        this.warnings += warnings
        if (errors > 0 || warnings > 0) {
            reports += reportUrl
        }
    }

    public String getMessage() {
        "$toolName rule violations were found ($errors errors, $warnings warnings). See the reports at:\n" +
                "${reports.collect { "- $it" }.join('\n')}"
    }
}
