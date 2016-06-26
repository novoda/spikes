package com.novoda.buildproperties

class BuildProperties {

    final String name
    File file
    Properties entries

    BuildProperties(String name) {
        this.name = name
    }

    String getName() {
        return name
    }

    File getFile() {
        return file
    }

    File getParentFile() {
        return file.getParentFile()
    }

    Properties getEntries() {
        return entries
    }

    void setFile(File file) {
        this.file = file
    }

    void setEntries(Properties entries) {
        this.entries = entries
    }

    void file(File file) {
        this.file = file
    }

}
