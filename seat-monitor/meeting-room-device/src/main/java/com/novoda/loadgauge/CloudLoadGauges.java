package com.novoda.loadgauge;

import java.util.Arrays;
import java.util.List;

public class CloudLoadGauges {

    private final List<CloudLoadGauge> loadGauges;

    public static CloudLoadGauges from(CloudLoadGauge... loadGauges) {
        return new CloudLoadGauges(Arrays.asList(loadGauges));
    }

    CloudLoadGauges(List<CloudLoadGauge> loadGauges) {
        this.loadGauges = loadGauges;
    }

    public void calibrateToZero() {
        for (CloudLoadGauge loadGauge : loadGauges) {
            loadGauge.calibrateToZero();
        }
    }

    public String asJsonArray() {
        StringBuilder jsonLoadGauges = new StringBuilder();

        for (CloudLoadGauge loadGauge : loadGauges) {
            jsonLoadGauges
                    .append(loadGauge.asJsonObject())
                    .append(",");
        }
        int lastCommaIndex = jsonLoadGauges.lastIndexOf(",");
        jsonLoadGauges.delete(lastCommaIndex, lastCommaIndex + 1);

        return "["
                + jsonLoadGauges.toString()
                + "]";
    }

    public int total() {
        return loadGauges.size();
    }
}
