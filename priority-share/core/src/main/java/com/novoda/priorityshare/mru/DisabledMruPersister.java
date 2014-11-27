package com.novoda.priorityshare.mru;

/**
 * Implementation of the MruPersister that doesn't store nor
 * retrieve any information and acts as a no-op.
 */
public class DisabledMruPersister implements MruPersister {

    @Override
    public void storeLastUsedTarget(String packageName) {
        // No-op
    }

    @Override
    public String getLastUsedTarget() {
        return PACKAGE_NONE;
    }

    @Override
    public void resetLastUsedTarget() {
        // No-op
    }

}
