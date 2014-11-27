package com.novoda.priorityshare;

import android.content.pm.ResolveInfo;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.SparseArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Represents a set of targets for a priority share action.
 */
public class TargetApps implements Parcelable {

    public static final String ACTION_SEND_WITH_PRIORITY = "com.novoda.priority.ACTION_SEND_WITH_PRIORITY";
    public static final String EXTRA_PRIORITY_APPS = "com.novoda.priority.extra.PRIORITY_APPS";

    public static final String APP_GOOGLE_PLUS = "com.google.android.apps.plus";
    public static final String APP_FACEBOOK = "com.facebook.katana";
    public static final String APP_FRIENDCASTER = "uk.co.senab.blueNotifyFree";
    public static final String APP_FRIENDCASTER_PRO = "uk.co.senab.blueNotify";
    public static final String APP_TWITTER = "com.twitter.android";
    public static final String APP_FALCON_PRO = "com.jv.falcon";
    public static final String APP_PLUME = "com.levelup.touiteur";
    public static final String APP_TALON = "com.klinker.android.twitter";
    public static final String APP_TWEEDLE = "com.handlerexploit.tweedle";
    public static final String APP_CARBON = "com.dotsandlines.carbon";
    public static final String APP_SEESMIC = "com.seesmic";
    public static final String APP_TWEETCASTER = "com.handmark.tweetcaster";
    public static final String APP_TWEETCASTER_PRO = "com.handmark.tweetcaster.premium";
    public static final String APP_TWEETLANES = "com.tweetlanes.android";

    private static final String[] FACEBOOK_PACKAGES;
    private static final String[] TWITTER_PACKAGES;
    private static final String[] GOOGLE_PLUS_PACKAGES;

    public static final TargetApps DEFAULTS;
    public static final TargetApps FACEBOOK;
    public static final TargetApps TWITTER;
    public static final TargetApps GOOGLE_PLUS;
    public static final TargetApps NONE;

    static {
        GOOGLE_PLUS_PACKAGES = new String[]{
                APP_GOOGLE_PLUS
        };
        FACEBOOK_PACKAGES = new String[]{
                APP_FACEBOOK, APP_FRIENDCASTER_PRO, APP_FRIENDCASTER
        };
        TWITTER_PACKAGES = new String[]{
                APP_TWITTER, APP_PLUME, APP_FALCON_PRO, APP_TALON, APP_CARBON, APP_TWEEDLE,
                APP_SEESMIC, APP_TWEETCASTER_PRO, APP_TWEETCASTER, APP_TWEETLANES
        };

        FACEBOOK = new TargetApps(FACEBOOK_PACKAGES);
        TWITTER = new TargetApps(TWITTER_PACKAGES);
        GOOGLE_PLUS = new TargetApps(GOOGLE_PLUS_PACKAGES);
        DEFAULTS = new TargetApps(FACEBOOK, TWITTER, GOOGLE_PLUS);
        NONE = new TargetApps(new String[0]);
    }

    private final Set<String> targetPackages;

    public static final Creator<TargetApps> CREATOR = new Creator<TargetApps>() {
        public TargetApps createFromParcel(Parcel in) {
            int arraySize = in.readInt();
            String[] packageNames = new String[arraySize];
            in.readStringArray(packageNames);

            return new TargetApps(packageNames);
        }

        public TargetApps[] newArray(int size) {
            return new TargetApps[size];
        }
    };

    public static TargetApps from(List<String>... targetPackages) {
        return new TargetApps(targetPackages);
    }

    public static TargetApps from(TargetApps... targets) {
        return new TargetApps(targets);
    }

    TargetApps(TargetApps... targets) {
        targetPackages = new LinkedHashSet<String>();
        for (TargetApps target : targets) {
            targetPackages.addAll(target.getTargetPackages());
        }
    }

    TargetApps(List<String>[] targetPackagesLists) {
        targetPackages = new LinkedHashSet<String>();
        for (List<String> targetPackageList : targetPackagesLists) {
            targetPackages.addAll(targetPackageList);
        }
    }

    TargetApps(String[]... packageNamesArrays) {
        targetPackages = new LinkedHashSet<String>();
        for (String[] packageNamesArray : packageNamesArrays) {
            targetPackages.addAll(Arrays.asList(packageNamesArray));
        }
    }

    public Set<String> getTargetPackages() {
        return new HashSet<String>(targetPackages);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        int arraySize = targetPackages.size();
        dest.writeInt(arraySize);

        String[] packagesArray = new String[arraySize];
        targetPackages.toArray(packagesArray);
        dest.writeStringArray(packagesArray);
    }

    public List<ResolveInfo> extractTargetResolveInfosFrom(List<ResolveInfo> resolveInfos) {
        SparseArray<ResolveInfo> matchedResolveInfos = new SparseArray<ResolveInfo>(targetPackages.size());
        for (ResolveInfo info : resolveInfos) {
            String packageName = info.activityInfo.packageName;
            int index = indexOfItemInto(packageName, targetPackages);
            if (index >= 0) {
                matchedResolveInfos.put(index, info);
            }
        }
        return sparseArrayToList(matchedResolveInfos);
    }

    private static <T> List<T> sparseArrayToList(SparseArray<T> sparseArray) {
        int sparseArraySize = sparseArray.size();
        ArrayList<T> list = new ArrayList<T>(sparseArraySize);
        for (int i = 0; i < sparseArraySize; i++) {
            list.add(sparseArray.valueAt(i));
        }
        return list;
    }

    private static <T> int indexOfItemInto(T item, Set<T> set) {
        int i = 0;
        for (T setItem : set) {
            if (setItem.equals(item)) {
                return i;
            }
            i++;
        }
        return -1;
    }

}
