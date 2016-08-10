package com.novoda.buildproperties

class Entry {

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
        getValue() as Integer
    }

    Double getDouble() {
        getValue() as Double
    }

    String getString() {
        getValue() as String
    }

    Object getValue() {
        value.call()
    }

}
