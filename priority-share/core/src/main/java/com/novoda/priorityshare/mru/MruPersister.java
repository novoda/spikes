package com.novoda.priorityshare.mru;

/**
 * A persister object that can be used to store and retrieve
 * MRU infos in a persistent storage.
 */
public interface MruPersister {

    public static final String PACKAGE_NONE = "";

    void storeLastUsedTarget(String packageName);

    String getLastUsedTarget();

    void resetLastUsedTarget();

}
