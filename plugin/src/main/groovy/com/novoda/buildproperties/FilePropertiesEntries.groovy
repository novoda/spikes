package com.novoda.buildproperties

class FilePropertiesEntries extends BuildProperties.Entries {

  private final String name
  private final File file
  private final Properties props

  static FilePropertiesEntries create(String name = null, File file) {
    Properties props = new Properties()
    props.load(new FileInputStream(file))
    new FilePropertiesEntries(name ?: file.name, file, props)
  }

  private FilePropertiesEntries(String name, File file, Properties props) {
    this.name = name
    this.file = file
    this.props = props
  }

  @Override
  boolean contains(String key) {
    props[key] != null
  }

  @Override
  protected Object getValueAt(String key) {
    Object value = props[key]
    if (value == null) {
      throw new IllegalArgumentException("No value defined for property '$key' in '$name' properties ($file.absolutePath)")
    }
    return value
  }

  @Override
  Set<String> keys() {
    props.stringPropertyNames()
  }

  @Override
  File file(String path) {
    new File(file, path)
  }

}
