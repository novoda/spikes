package com.novoda.tpbot.feature_selection;

public interface FeatureSelectionPersistence {

    boolean isFeatureEnabled();

    void setFeatureEnabled();

    void setFeatureDisabled();

}
