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

    private Object getValue() {
        value.call()
    }

    Entry or(def fallback) {
        def other = from(fallback)
        new Entry(key, {
            try {
                return getValue()
            } catch (Throwable e) {
                try {
                    return other.call()
                } catch (Throwable e2) {
                    throw CompositeException.from(e).add(e2)
                }
            }
        })
    }

    private static def from(def fallback) {
        if (fallback instanceof Entry) {
            return { (fallback as Entry).getValue() }
        }
        if (fallback instanceof Closure) {
            return fallback
        }
        return { fallback }
    }

}
