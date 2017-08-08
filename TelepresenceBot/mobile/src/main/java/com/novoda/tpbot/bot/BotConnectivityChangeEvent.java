package com.novoda.tpbot.bot;

final class BotConnectivityChangeEvent {

    private static final boolean WITH_CONNECTION = true;
    private static final boolean WITHOUT_CONNECTION = false;
    private static final String WITHOUT_REASON = "";

    private boolean isConnected;
    private String reason;

    public static BotConnectivityChangeEvent notConnected(String reason) {
        return new BotConnectivityChangeEvent(WITHOUT_CONNECTION, reason);
    }

    public static BotConnectivityChangeEvent connected() {
        return new BotConnectivityChangeEvent(WITH_CONNECTION, WITHOUT_REASON);
    }

    private BotConnectivityChangeEvent(boolean isConnected, String reason) {
        this.isConnected = isConnected;
        this.reason = reason;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public String reason() {
        return reason;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        BotConnectivityChangeEvent that = (BotConnectivityChangeEvent) o;

        if (isConnected != that.isConnected) {
            return false;
        }
        return reason != null ? reason.equals(that.reason) : that.reason == null;

    }

    @Override
    public int hashCode() {
        int result = (isConnected ? 1 : 0);
        result = 31 * result + (reason != null ? reason.hashCode() : 0);
        return result;
    }
}
