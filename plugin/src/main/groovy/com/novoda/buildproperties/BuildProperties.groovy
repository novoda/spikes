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
    entries.call().parentFile
  }

  Enumeration<String> getKeys() {
    entries.call().keys
  }

  Entry getAt(String key) {
    if (project.hasProperty(key)) {
      new Entry(key, { project[key] })
    } else {
      entries.call().getAt(key)
    }
  }

  static abstract class Entries {

    abstract boolean contains(String key)

    protected abstract Object getValueAt(String key)

    Entry getAt(String key) {
      new Entry(key, {
        getValueAt(key)
      })
    }

    abstract File getParentFile()

    abstract Enumeration<String> getKeys()

  }

  static class Entry {
    private final String key
    private final Closure<Object> value

    Entry(String key, Closure<Object> value) {
      this.key = key
      this.value = value
    }

    String getKey() {
      key
    }

    Boolean getBoolean() {
      Boolean.parseBoolean(string)
    }

    Integer getInt() {
      value.call() as Integer
    }

    Double getDouble() {
      value.call() as Double
    }

    String getString() {
      value.call() as String
    }

    Object getValue() {
      value.call()
    }
  }

}
