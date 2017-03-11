package com.novoda.tpbot.support;

public enum ClientType {

    BOT("clientType=bot"),
    HUMAN("clientType=human");

    private final String rawClientType;

    ClientType(String rawClientType) {
        this.rawClientType = rawClientType;
    }

    public String rawClientType() {
        return rawClientType;
    }

}
