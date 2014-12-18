package com.novoda.comparereports

import com.novoda.comparereports.bean.*

class Reporter {

    static Report generate(Checkstyle mainCheckstyle, Checkstyle currentCheckstyle, String mainCheckstyleBaseDir, String currentCheckstyleBaseDir) {
        List<ReportIssue> mainBranchIssues = new PathProcessor(mainCheckstyleBaseDir).process(from(mainCheckstyle))
        List<ReportIssue> currentBranchIssues = new PathProcessor(currentCheckstyleBaseDir).process(from(currentCheckstyle))

        FixedIssues fixedIssues = mainBranchIssues.findAll { !currentBranchIssues.contains(it) } ?: new FixedIssues()
        IntroducedIssues introducedIssues = currentBranchIssues.findAll { !mainBranchIssues.contains(it) } ?: new IntroducedIssues()

        return new Report(fixedIssues, introducedIssues)
    }

    static List<ReportIssue> from(Checkstyle checkstyle) {
        return checkstyle.files.collect { File file ->
            file.errors?.collect { Issue error ->
                new ReportIssue(new ReportFile(file.name), error.line, error.column, error.severity, error.message, error.source)
            } ?: new ArrayList<ReportIssue>()
        }.flatten()
    }

}
