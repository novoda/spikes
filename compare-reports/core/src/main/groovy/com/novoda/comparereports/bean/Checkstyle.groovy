package com.novoda.comparereports.bean

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement

@JacksonXmlRootElement(localName = "checkstyle")
class Checkstyle {

    final String version

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "file")
    final List<File> files

    // This is used by Jackson
    Checkstyle() {}

    Checkstyle(String version, List<File> files) {
        this.version = version
        this.files = files
    }

    @Override
    public String toString() {
        return "Checkstyle{" +
                "version='" + version + '\'' +
                ", files=" + files +
                '}';
    }

}
