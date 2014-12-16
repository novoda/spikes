package com.novoda.comparereports.bean

class IntroducedIssues extends ArrayList<ReportIssue> {

    IntroducedIssues(Collection<ReportIssue> collection) {
        super(collection)
    }

    String forHumans() {
        StringBuilder builder = new StringBuilder()
        builder.append("This is the list of things you've introduced so far:\n")
        builder.append("----------------------------------------------------\n")

        each { ReportIssue issue ->
            builder.append(" > [${issue.severity}]: ${issue.message}\n")
            builder.append("\tIn: ${issue.file.name}:${issue.line}\n")
            builder.append("\n")
        }
        builder.append("-----------------------------------------------\n").toString()
    }

}
