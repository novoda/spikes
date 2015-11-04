package com.novoda.easycustomtabs.provider;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.support.annotation.WorkerThread;
import android.support.customtabs.CustomTabsService;
import android.text.TextUtils;

import com.novoda.easycustomtabs.EasyCustomTabs;

import java.util.ArrayList;
import java.util.List;

class BestPackageFinder {

    private static final String NO_PACKAGE_FOUND = "";
    private static final String ANY_URL = "http://www.example.com";
    private static final Intent INTENT_TO_EXTERNAL_LINK = new Intent(Intent.ACTION_VIEW, Uri.parse(ANY_URL));

    private final PackageManager packageManager;

    BestPackageFinder(PackageManager packageManager) {
        this.packageManager = packageManager;
    }

    public static BestPackageFinder newInstance() {
        Context context = EasyCustomTabs.getInstance().getContext();
        PackageManager packageManager = context.getPackageManager();
        return new BestPackageFinder(packageManager);
    }

    @WorkerThread
    public String findBestPackage() {
        List<String> packagesSupportingCustomTabs = getPackagesSupportingCustomTabs();
        if (packagesSupportingCustomTabs.isEmpty()) {
            return NO_PACKAGE_FOUND;
        }

        String defaultPackage = getDefaultPackage();
        if (packagesSupportingCustomTabs.contains(defaultPackage)) {
            return defaultPackage;
        }

        return packagesSupportingCustomTabs.get(0);
    }

    private String getDefaultPackage() {
        ResolveInfo defaultActivityInfo = packageManager.resolveActivity(INTENT_TO_EXTERNAL_LINK, 0);

        if (defaultActivityInfo == null) {
            return NO_PACKAGE_FOUND;
        }

        String packageName = defaultActivityInfo.activityInfo.packageName;

        return TextUtils.isEmpty(packageName) ? NO_PACKAGE_FOUND : packageName;
    }

    private List<String> getPackagesSupportingCustomTabs() {
        List<ResolveInfo> resolvedInfoList = packageManager.queryIntentActivities(INTENT_TO_EXTERNAL_LINK, 0);
        List<String> packagesSupportingCustomTabs = new ArrayList<>();
        for (ResolveInfo info : resolvedInfoList) {
            Intent serviceIntent = new Intent();
            serviceIntent.setAction(CustomTabsService.ACTION_CUSTOM_TABS_CONNECTION);
            serviceIntent.setPackage(info.activityInfo.packageName);
            if (packageManager.resolveService(serviceIntent, 0) != null) {
                packagesSupportingCustomTabs.add(info.activityInfo.packageName);
            }
        }

        return packagesSupportingCustomTabs;
    }

}
