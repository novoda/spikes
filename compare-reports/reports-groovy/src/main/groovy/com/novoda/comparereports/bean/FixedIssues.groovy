package com.novoda.comparereports.bean

class FixedIssues extends ArrayList<ReportIssue> {

    FixedIssues() {
        super()
    }

    FixedIssues(Collection<ReportIssue> collection) {
        super(collection)
    }

    String forHumans() {
        if (isEmpty()) {
            return "You haven't fixed any issue :O"
        }

        StringBuilder builder = new StringBuilder()
        builder.append("This is the list of things you've fixed so far:\n")
        builder.append("-----------------------------------------------\n")
        each { ReportIssue issue ->
            builder.append(" > [${issue.severity}]: ${issue.message} | In: ${issue.file.name}:${issue.line}:${issue.column}\n")
        }
        builder.toString()
    }

}
