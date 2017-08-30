package com.novoda.tpbot.feature_selection;

public interface FeatureSelectionController<LIST, FEATURE> {

    void attachFeatureSelectionTo(LIST toAttachTo);

    void handleFeatureToggle(FEATURE featureRepresentation);

    boolean contains(FEATURE featureRepresentation);

}
