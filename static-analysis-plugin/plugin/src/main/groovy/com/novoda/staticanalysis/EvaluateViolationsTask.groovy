package com.novoda.staticanalysis

import com.novoda.staticanalysis.internal.Violations
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.tasks.TaskAction

class EvaluateViolationsTask extends DefaultTask {
    private PenaltyExtension penalty
    private NamedDomainObjectContainer<Violations> allViolations

    EvaluateViolationsTask() {
        group = 'verification'
        description = 'Evaluate total violations against penalty thresholds.'
    }

    void setPenalty(PenaltyExtension penalty) {
        this.penalty = penalty
    }

    void setAllViolations(NamedDomainObjectContainer<Violations> allViolations) {
        this.allViolations = allViolations
    }

    @TaskAction
    public void run() {
        Map<String, Integer> total = [errors: 0, warnings: 0]
        String fullMessage = '\n'
        allViolations.each { Violations violations ->
            int errors = violations.errors
            int warnings = violations.warnings
            if (errors > 0 || warnings > 0) {
                fullMessage += "> $violations.message\n"
                total['errors'] += errors
                total['warnings'] += warnings
            }
        }
        int errorsDiff = Math.max(0, total['errors'] - penalty.maxErrors)
        int warningsDiff = Math.max(0, total['warnings'] - penalty.maxWarnings)
        if (errorsDiff > 0 || warningsDiff > 0) {
            throw new GradleException("Violations limit exceeded by $errorsDiff errors, $warningsDiff warnings.\n$fullMessage")
        } else {
            project.logger.warn fullMessage
        }
    }
}
