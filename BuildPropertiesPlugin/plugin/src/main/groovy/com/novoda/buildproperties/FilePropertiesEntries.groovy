package com.novoda.buildproperties

class FilePropertiesEntries extends BuildProperties.Entries {

  private final String name
  private final File file
  private final Properties props
  private final FilePropertiesEntries defaults
  private final Set<String> keys

  static FilePropertiesEntries create(String name = null, File file) {
    Properties props = new Properties()
    props.load(new FileInputStream(file))

    FilePropertiesEntries defaults = null
    String include = props['include']
    if (include != null) {
      defaults = create(new File(file.parentFile, include))
    }
    new FilePropertiesEntries(name ?: file.name, file, props, defaults)
  }

  private FilePropertiesEntries(String name, File file, Properties props, FilePropertiesEntries defaults = null) {
    this.name = name
    this.file = file
    this.props = props
    this.defaults = defaults
    this.keys = new HashSet<>(props.stringPropertyNames())
    if (defaults != null) {
      this.keys.addAll(defaults.keys)
    }
  }

  @Override
  boolean contains(String key) {
    props[key] != null || defaults?.contains(key)
  }

  @Override
  protected Object getValueAt(String key) {
    Object value = props[key]
    if (value != null) {
      return value
    }
    if (defaults?.contains(key)) {
      return defaults.getValueAt(key)
    }
    throw new IllegalArgumentException("No value defined for property '$key' in '$name' properties ($file.absolutePath)")
  }

  @Override
  File getParentFile() {
    file.parentFile
  }

  @Override
  Enumeration<String> getKeys() {
    Collections.enumeration(keys)
  }

}
