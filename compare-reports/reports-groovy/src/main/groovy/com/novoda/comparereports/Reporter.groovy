package com.novoda.comparereports

import com.novoda.comparereports.bean.*

class Reporter {

    static Report generate(Checkstyle oldCheckstyle, Checkstyle newCheckstyle) {
        List<ReportIssue> oldIssues = from(oldCheckstyle)
        List<ReportIssue> newIssues = from(newCheckstyle)
        FixedIssues fixedIssues = oldIssues.findAll { !newIssues.contains(it) }
        IntroducedIssues introducedIssues = newIssues.findAll { !oldIssues.contains(it) }
        return new Report(fixedIssues, introducedIssues)
    }

    static List<ReportIssue> from(Checkstyle checkstyle) {
        return checkstyle.files.collect { File file ->
            file.errors?.collect { Issue error ->
                new ReportIssue(new ReportFile(file.name), error.line, error.severity, error.message, error.source)
            } ?: new ArrayList<ReportIssue>()
        }.flatten()
    }

}
