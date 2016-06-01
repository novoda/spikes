package com.novoda.bonfire.channel.database;

class FirebaseChannel {
    private String name;
    private String access;

    @SuppressWarnings("unused") // used by Firebase
    public FirebaseChannel() {
    }

    public FirebaseChannel(String name, String access) {
        this.name = name;
        this.access = access;
    }

    public String getName() {
        return name;
    }

    public String getAccess() {
        return access;
    }
}
