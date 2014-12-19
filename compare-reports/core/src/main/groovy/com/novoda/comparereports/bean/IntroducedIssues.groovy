package com.novoda.comparereports.bean

class IntroducedIssues extends ArrayList<ReportIssue> {

    IntroducedIssues() {
        super()
    }

    IntroducedIssues(Collection<ReportIssue> collection) {
        super(collection)
    }

    String forHumans() {
        if (isEmpty()) {
            return "You haven't introduced any issue \\O/"
        }

        StringBuilder builder = new StringBuilder()
        builder.append("This is the list of things you've introduced so far:\n")
        builder.append("----------------------------------------------------\n")
        each { ReportIssue issue ->
            builder.append(" > [${issue.severity}]: ${issue.message} | In: ${issue.file.name}:${issue.line}:${issue.column}\n")
        }
        builder.toString()
    }

}
