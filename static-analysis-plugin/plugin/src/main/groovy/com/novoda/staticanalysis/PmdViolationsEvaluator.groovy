package com.novoda.staticanalysis

import groovy.util.slurpersupport.GPathResult

class PmdViolationsEvaluator {

    private final GPathResult xml

    PmdViolationsEvaluator(File report) {
        this(new XmlSlurper().parse(report))
    }

    PmdViolationsEvaluator(GPathResult xml) {
        this.xml = xml
    }

    Set<PmdViolation> collectViolations() {
        Collection files = xml.'**'.findAll { node -> node.name() == 'file' }
        files.inject(new HashSet<PmdViolation>()) { HashSet<PmdViolation> violations, file ->
            violations += file.'**'
                    .findAll { violation -> violation.name() == 'violation' }
                    .collect { violation -> new PmdViolation(violation, file.@name as String) }
            violations
        }
    }

    static class PmdViolation {
        String beginLine
        String endLine
        String beginColumn
        String endColumn
        String rule
        String ruleSet
        String className
        String methodName
        String priority

        PmdViolation(def violation, String file) {
            this(violation.@beginline as String,
                    violation.@endline as String,
                    violation.@begincolumn as String,
                    violation.@endcolumn as String,
                    violation.@rule as String,
                    violation.@ruleset as String,
                    file,
                    violation.@method as String,
                    violation.@priority as String)
        }

        PmdViolation(String beginLine,
                     String endLine,
                     String beginColumn,
                     String endColumn,
                     String rule,
                     String ruleSet,
                     String className,
                     String methodName,
                     String priority) {
            this.beginLine = beginLine
            this.endLine = endLine
            this.beginColumn = beginColumn
            this.endColumn = endColumn
            this.rule = rule
            this.ruleSet = ruleSet
            this.className = className
            this.methodName = methodName
            this.priority = priority
        }

        boolean isError() {
            priority == '1' || priority == '2'
        }

        boolean equals(o) {
            if (this.is(o)) return true
            if (getClass() != o.class) return false

            PmdViolation that = (PmdViolation) o

            if (beginColumn != that.beginColumn) return false
            if (beginLine != that.beginLine) return false
            if (className != that.className) return false
            if (endColumn != that.endColumn) return false
            if (endLine != that.endLine) return false
            if (methodName != that.methodName) return false
            if (priority != that.priority) return false
            if (rule != that.rule) return false
            if (ruleSet != that.ruleSet) return false

            return true
        }

        int hashCode() {
            int result
            result = beginLine.hashCode()
            result = 31 * result + endLine.hashCode()
            result = 31 * result + beginColumn.hashCode()
            result = 31 * result + endColumn.hashCode()
            result = 31 * result + rule.hashCode()
            result = 31 * result + ruleSet.hashCode()
            result = 31 * result + className.hashCode()
            result = 31 * result + (methodName != null ? methodName.hashCode() : 0)
            result = 31 * result + priority.hashCode()
            return result
        }

        @Override
        public String toString() {
            return "PmdViolation{" +
                    "beginLine='" + beginLine + '\'' +
                    ", endLine='" + endLine + '\'' +
                    ", beginColumn='" + beginColumn + '\'' +
                    ", endColumn='" + endColumn + '\'' +
                    ", rule='" + rule + '\'' +
                    ", ruleSet='" + ruleSet + '\'' +
                    ", className='" + className + '\'' +
                    ", methodName='" + methodName + '\'' +
                    ", priority='" + priority + '\'' +
                    "} " + super.toString();
        }
    }

}
