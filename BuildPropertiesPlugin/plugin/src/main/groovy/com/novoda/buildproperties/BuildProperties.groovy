package com.novoda.buildproperties

import org.gradle.api.GradleException
import org.gradle.api.Project

class BuildProperties {

  private final String name
  private final Project project
  private Entries entries

  BuildProperties(String name, Project project) {
    this.name = name
    this.project = project
  }

  String getName() {
    name
  }

  void file(File file, String errorMessage = null) {
    if (!file.exists()) {
      throw new GradleException("File $file.name does not exist.${errorMessage ? "\n$errorMessage" : ''}")
    }
    entries(FilePropertiesEntries.create(name, file))
  }

  void entries(Entries entries) {
    this.entries = entries
  }

  File getParentFile() {
    entries.parentFile
  }

  Enumeration<String> getKeys() {
    entries.keys
  }

  Entry getAt(String key) {
    if (project.hasProperty(key)) {
      new Entry(key, { project[key] })
    } else {
      entries.getAt(key)
    }
  }

}
