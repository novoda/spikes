package com.novoda.easycustomtabs.provider;

import android.support.annotation.NonNull;

public interface AvailableAppProvider {

    void findBestPackage(@NonNull EasyCustomTabsAvailableAppProvider.PackageFoundCallback packageFoundCallback);

}
