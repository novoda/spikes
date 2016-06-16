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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        FirebaseChannel that = (FirebaseChannel) o;

        return name.equals(that.name) && access.equals(that.access);

    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + access.hashCode();
        return result;
    }
}
