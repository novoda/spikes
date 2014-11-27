package com.novoda.priorityshare;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ShareTargetsAdapter extends BaseAdapter {

    private final List<ResolveInfo> priorityAppInfos;
    private final PackageManager packageManager;
    private final LayoutInflater layoutInflater;
    private final List<ResolveInfo> otherAppInfos;

    private boolean showAllApps;
    private int iconSizePx;

    ShareTargetsAdapter(List<ResolveInfo> priorityAppInfos, List<ResolveInfo> otherAppInfos, PackageManager packageManager, LayoutInflater layoutInflater, int iconSizePx) {
        this.priorityAppInfos = priorityAppInfos;
        this.otherAppInfos = otherAppInfos;
        this.packageManager = packageManager;
        this.layoutInflater = layoutInflater;
        this.iconSizePx = iconSizePx;
    }

    public static ShareTargetsAdapter newInstance(Context context, List<ResolveInfo> priorityAppInfos, List<ResolveInfo> otherAppInfos) {
        PackageManager packageManager = context.getPackageManager();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        otherAppInfos = sortResolveInfoList(otherAppInfos, context.getPackageManager());
        int iconSizePx = getIconSizePx(context.getResources());

        return new ShareTargetsAdapter(priorityAppInfos, otherAppInfos, packageManager, layoutInflater, iconSizePx);
    }

    private static int getIconSizePx(Resources resources) {
        return resources.getDimensionPixelSize(R.dimen.ps__grid_item_icon_size);
    }

    private static List<ResolveInfo> sortResolveInfoList(List<ResolveInfo> resolveInfoList, PackageManager packageManager) {
        ArrayList<ResolveInfo> sortedList = new ArrayList<ResolveInfo>(resolveInfoList);
        Collections.sort(sortedList, new ResolveInfo.DisplayNameComparator(packageManager));
        return sortedList;
    }

    public void showAllApps() {
        showAllApps = true;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        int otherAppsCountMaybe = showAllApps ? otherAppInfos.size() : 0;
        return priorityAppInfos.size() + otherAppsCountMaybe;
    }

    @Override
    public ResolveInfo getItem(int position) {
        int firstOtherAppsItemPosition = priorityAppInfos.size();
        if (showAllApps && position >= firstOtherAppsItemPosition) {
            int offsetPosition = position - firstOtherAppsItemPosition;
            return otherAppInfos.get(offsetPosition);
        }
        return priorityAppInfos.get(position);
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getViewForTargetAppItem(position, convertView, parent);
    }

    private TextView initializeItemViewIfNecessary(View convertView, int layoutResId, ViewGroup parent) {
        TextView textView;
        if (convertView instanceof TextView) {
            textView = (TextView) convertView;
        } else {
            textView = (TextView) layoutInflater.inflate(layoutResId, parent, false);
        }
        return textView;
    }

    private View getViewForTargetAppItem(int position, View convertView, ViewGroup parent) {
        TextView textView = initializeItemViewIfNecessary(convertView, R.layout.ps__grid_item_share, parent);

        ResolveInfo info = getItem(position);
        updateItemForPriorityInfo(textView, info, iconSizePx);

        return textView;
    }

    private void updateItemForPriorityInfo(TextView textView, ResolveInfo info, int iconSizePx) {
        textView.setText(info.loadLabel(packageManager));
        Drawable icon = info.loadIcon(packageManager);
        icon = enforceIconSize(icon, iconSizePx);
        textView.setCompoundDrawables(null, icon, null, null);
    }

    private Drawable enforceIconSize(Drawable icon, int iconSizePx) {
        icon.mutate();
        icon.setBounds(0, 0, iconSizePx, iconSizePx);
        return icon;
    }

}
