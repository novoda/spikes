package com.novoda.tpbot.model;

public enum ClientType {

    BOT("clientType=bot"),
    HUMAN("clientType=human");

    private final String rawQuery;

    ClientType(String rawQuery) {
        this.rawQuery = rawQuery;
    }

    public String rawQuery() {
        return rawQuery;
    }

}
