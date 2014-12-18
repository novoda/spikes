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
            String fileNameUsingRelativePath = issue.file.name.substring(baseDir.length())
            new ReportIssue(new ReportFile(fileNameUsingRelativePath), issue.line, issue.column, issue.severity, issue.message, issue.source)
        }
    }
}
