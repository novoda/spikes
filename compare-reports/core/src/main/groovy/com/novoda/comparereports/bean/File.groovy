package com.novoda.comparereports.bean

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement

@JacksonXmlRootElement(localName = "file")
class File {

    final String name

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "error")
    final List<Issue> errors

    File(String name, List<Issue> errors) {
        this.name = name
        this.errors = errors
    }

    // This is used by Jackson
    File() {}

    @Override
    public String toString() {
        return "File{" +
                "name='" + name + '\'' +
                ", errors=" + errors +
                '}';
    }
}
