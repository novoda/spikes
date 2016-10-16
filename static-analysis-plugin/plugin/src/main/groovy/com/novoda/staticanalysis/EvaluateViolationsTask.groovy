package com.novoda.staticanalysis

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction

class EvaluateViolationsTask extends DefaultTask {
    public PenaltyExtension penalty
    public Class<?> tool
    private int errors = 0
    private int warnings = 0
    private List<String> reports = []

    public void addViolations(int errors, int warnings, String reportUrl) {
        this.errors += errors
        this.warnings += warnings
        if (errors > 0 || warnings > 0) {
            reports += reportUrl
        }
    }

    @TaskAction
    public void run() {
        if (errors == 0 && warnings == 0) {
            return
        }
        String message = "$tool.simpleName rule violations were found ($errors errors, $warnings warnings). See the reports at:\n" +
                "${reports.collect { "- $it" }.join('\n')}"
        if (errors > penalty.maxErrors || warnings > penalty.maxWarnings) {
            throw new GradleException(message)
        } else {
            project.logger.warn(message)
        }
    }
}
