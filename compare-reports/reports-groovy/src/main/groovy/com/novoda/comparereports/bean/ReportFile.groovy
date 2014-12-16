package com.novoda.comparereports.bean

class ReportFile {

    final String name

    ReportFile(String name) {
        this.name = name
    }

    @Override
    public String toString() {
        return "ReportFile{" +
                "name='" + name + '\'' +
                '}';
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        ReportFile that = (ReportFile) o

        if (name != that.name) return false

        return true
    }

    int hashCode() {
        return name.hashCode()
    }
}
