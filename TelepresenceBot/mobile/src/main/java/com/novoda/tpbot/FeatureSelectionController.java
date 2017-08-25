package com.novoda.tpbot;

interface FeatureSelectionController<LIST, FEATURE> {

    void attachFeatureSelectionTo(LIST toAttachTo);

    void handleFeatureToggle(FEATURE featureRepresentation);

    boolean contains(FEATURE featureRepresentation);

}
