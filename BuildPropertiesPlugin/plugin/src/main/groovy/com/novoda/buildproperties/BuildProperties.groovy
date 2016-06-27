package com.novoda.buildproperties

class BuildProperties {

    private final String name
    private File file
    private Closure<Properties> entries

    static BuildProperties create(File file) {
        BuildProperties buildProperties = new BuildProperties(file.absolutePath)
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

    static class Entry {
        final String key
        final Closure<Object> value

        Entry(String key, Closure<Object> value) {
            this.key = key
            this.value = value
        }

        Boolean booleanValue() {
            value.call() as Boolean
        }

        Integer intValue() {
           value.call() as Integer
        }

        Double doubleValue() {
            value.call() as Double
        }

        String stringValue() {
            value.call() as String
        }
    }

}
