package com.novoda.tpbot;

interface FeatureSelectionPersistence {

    boolean isFeatureEnabled();

    void setFeatureEnabled();

    void setFeatureDisabled();

}
