package com.novoda.buildproperties

class BuildProperties {

  private final String name
  private Closure<Entries> entries

  BuildProperties(String name) {
    this.name = name
  }

  String getName() {
    name
  }

  void file(File file) {
    this.entries = {
      FilePropertiesEntries.create(name, file)
    }.memoize()
  }

  File getParentFile() {
    entries.call().parentFile
  }

  Set<String> getKeys() {
    entries.call().keys
  }

  Entry getAt(String key) {
    entries.call().getAt(key)
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

    abstract Set<String> getKeys()

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
