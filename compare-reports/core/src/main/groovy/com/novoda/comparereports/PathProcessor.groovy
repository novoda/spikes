package com.novoda.comparereports

import com.novoda.comparereports.bean.ReportFile
import com.novoda.comparereports.bean.ReportIssue

class PathProcessor {

    final String baseDir

    PathProcessor(String baseDir) {
        this.baseDir = baseDir
    }

    List<ReportIssue> process(List<ReportIssue> reportIssues) {
        reportIssues.collect { ReportIssue issue ->
            String fileNameUsingRelativePath = getRelativePath(issue)
            new ReportIssue(new ReportFile(fileNameUsingRelativePath), issue.line, issue.column, issue.severity, issue.message, issue.source)
        }
    }

    private String getRelativePath(ReportIssue issue) {
        issue.file.name.substring(baseDir.length() + 1) // + 1 because it shouldn't include the first '/'
    }
}
