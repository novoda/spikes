package com.novoda.comparereports.bean

class Report {

    private static final String CHECKSTYLE_REPORT_VERSION = "5.7"

    final FixedIssues fixedIssues
    final IntroducedIssues introducedIssues

    Report(FixedIssues fixedIssues, IntroducedIssues introducedIssues) {
        this.fixedIssues = fixedIssues
        this.introducedIssues = introducedIssues
    }

    Checkstyle fixedCheckstyle() {
        toCheckstyle(fixedIssues)
    }

    Checkstyle introducedCheckstyle() {
        toCheckstyle(introducedIssues)
    }

    Checkstyle toCheckstyle(List<ReportIssue> reportIssues) {
        Set<ReportFile> reportFiles = reportIssues.collect { ReportIssue issue -> issue.file }.toSet()
        List<File> files = reportFiles.collect { ReportFile file ->
            List<ReportIssue> issuesForFile = reportIssues.findAll { ReportIssue issue -> issue.file.equals(file) }
            List<Issue> issues = issuesForFile.collect { ReportIssue issue -> issue.toIssue() }
            new File(file.name, issues)
        }

        new Checkstyle(CHECKSTYLE_REPORT_VERSION, files)
    }

    @Override
    public String toString() {
        return "Report{" +
                "fixedIssues=" + fixedIssues +
                ", introducedIssues=" + introducedIssues +
                '}';
    }
}
