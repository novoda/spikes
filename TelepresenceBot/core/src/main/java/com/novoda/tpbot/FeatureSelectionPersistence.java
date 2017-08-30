package com.novoda.tpbot;

public interface FeatureSelectionPersistence {

    boolean isFeatureEnabled();

    void setFeatureEnabled();

    void setFeatureDisabled();

}
