package com.novoda.tpbot;

public interface FeatureSelectionController<LIST, FEATURE> {

    void attachFeaturesTo(LIST componentToAttachTo);

    void handleFeatureToggle(FEATURE featureRepresentation);

    boolean contains(FEATURE featureRepresentation);

}
