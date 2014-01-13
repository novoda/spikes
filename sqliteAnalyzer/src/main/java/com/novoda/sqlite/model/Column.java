package com.novoda.sqlite.model;

public final class Column {
    private final String name;
    private final String type;
    private boolean nullable;
    private DataAffinity affinity;

    public Column(String name, String type, boolean nullable, DataAffinity affinity) {
        this.name = name;
        this.type = type;
        this.nullable = nullable;
        this.affinity = affinity;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public boolean isNullable() {
        return nullable;
    }
}
