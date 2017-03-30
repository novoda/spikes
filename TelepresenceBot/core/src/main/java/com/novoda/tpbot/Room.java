package com.novoda.tpbot;

public enum Room {

    LONDON("room=London");

    private final String rawQuery;

    Room(String rawQuery) {
        this.rawQuery = rawQuery;
    }

    public String rawQuery() {
        return rawQuery;
    }

}
