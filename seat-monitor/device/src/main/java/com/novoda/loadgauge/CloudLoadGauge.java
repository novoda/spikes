package com.novoda.loadgauge;

public class CloudLoadGauge {

    private final String id;
    private final LoadGauge loadGauge;

    public CloudLoadGauge(String id, LoadGauge loadGauge) {
        this.id = id;
        this.loadGauge = loadGauge;
    }

    public void calibrateToZero() {
        loadGauge.calibrateToZero();
    }

    public String asJsonObject() {
        return "{"
                + "\"id\": \"" + id + "\", "
                + "\"weight\":" + loadGauge.readWeight() + ""
                + "}";
    }
}
