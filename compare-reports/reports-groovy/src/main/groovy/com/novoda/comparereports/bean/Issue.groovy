package com.novoda.comparereports.bean

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement

@JacksonXmlRootElement(localName = "error")
class Issue {

    final Number line
    final Number column
    final String severity
    final String message
    final String source

    Issue(Number line, Number column, String severity, String message, String source) {
        this.line = line
        this.column = column
        this.severity = severity
        this.message = message
        this.source = source
    }

    // This is used by Jackson
    Issue() {}

    @Override
    public String toString() {
        return "Issue{" +
                "line=" + line +
                ", column=" + column + '\'' +
                ", severity='" + severity + '\'' +
                ", message='" + message + '\'' +
                ", source='" + source + '\'' +
                '}';
    }
}
