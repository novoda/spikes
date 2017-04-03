package com.novoda.pianohero;

import java.util.Iterator;
import java.util.List;

class RoundViewModel implements Iterable<String> {
    private final List<String> notations;
    private final String statusMessage;

    RoundViewModel(List<String> notations, String statusMessage) {
        this.notations = notations;
        this.statusMessage = statusMessage;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    @Override
    public Iterator<String> iterator() {
        return notations.iterator();
    }
}
