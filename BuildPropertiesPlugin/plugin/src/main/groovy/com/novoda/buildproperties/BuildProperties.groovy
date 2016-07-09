package com.novoda.buildproperties

class BuildProperties {

  private final String name
  private File file
  private Closure<Properties> entries

  static BuildProperties create(String name = null, File file) {
    BuildProperties buildProperties = new BuildProperties(name ?: file.name)
    buildProperties.file file
    return buildProperties
  }

  BuildProperties(String name) {
    this.name = name
  }

  String getName() {
    return name
  }

  File getFile() {
    return file
  }

  void file(File file) {
    this.file = file
    this.entries = {
      Properties properties = new Properties()
      properties.load(new FileInputStream(file))
      properties
    }.memoize()
  }

  File getParentFile() {
    return file.getParentFile()
  }

  Collection<Entry> allEntries() {
    Properties properties = entries.call()
    properties.stringPropertyNames().collect { String name ->
      return new Entry(name, { properties[name] })
    }
  }

  Entry getAt(String key) {
    def getValue = {
      Object value = entries.call()[key]
      if (value == null) {
        throw new IllegalArgumentException("No value defined for property '$key' in '$name' properties ($file.absolutePath)")
      }
      return value
    }
    return new Entry(key, getValue)
  }

  static abstract class Entries implements Iterable<Entry> {

    abstract boolean contains(String key)

    protected abstract Object getValueAt(String key)

    Entry getAt(String key) {
      new Entry(key, {
        getValueAt(key)
      })
    }

    abstract File parentFile()

    abstract Set<String> keys()

    @Override
    Iterator<Entry> iterator() {
      keys().collect { String key ->
        getAt(key)
      }.iterator()
    }

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
