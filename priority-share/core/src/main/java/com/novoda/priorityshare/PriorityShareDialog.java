package com.novoda.priorityshare;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListAdapter;

import com.novoda.priorityshare.mru.DisabledMruPersister;
import com.novoda.priorityshare.mru.MruPersister;
import com.novoda.priorityshare.mru.SharedPreferencesMruPersister;

import java.util.ArrayList;
import java.util.List;

public class PriorityShareDialog extends DialogFragment {

    public static final String TAG_PRIORITY_SHARE_DIALOG = "priority_share_dialog";

    private static final String ARGS_KEY_TITLE = "title";
    private static final String ARGS_KEY_THEME_RES_ID = "theme_res_id";
    private static final String ARGS_KEY_TARGET_APPS = "target_apps";
    private static final String ARGS_KEY_SHARING_INTENT = "sharing_intent";
    private static final String ARGS_KEY_SHOW_MRU = "show_mru";

    private static final String KEY_SHOWING_ALL_SHARE_TARGETS = "showing_all";

    private ShareTargetsAdapter adapter;
    private Intent sharingIntent;
    private OnPrepareSharingIntentListener listener;
    private MruPersister persister;
    private boolean showMru;
    private boolean showingAllShareTargets;
    private ViewGroup dialogView;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();
        sharingIntent = args.getParcelable(ARGS_KEY_SHARING_INTENT);

        showMru = extractShowMruFrom(args);
        persister = createMruPersister(getActivity(), showMru);

        adapter = createShareTargetsAdapterFrom(args, sharingIntent);

        int themeResId = getThemeResIdIfProvidedIn(args);
        CharSequence title = getTitleIfProvidedIn(args);

        dialogView = createDialogView();
        setupGridView(dialogView, adapter);
        restoreShowingAllShareTargetsState(savedInstanceState);
        tryShowingContentOrSwitchToEmptyStateUi(adapter);

        return createDialog(themeResId, title, dialogView);
    }

    private boolean extractShowMruFrom(Bundle args) {
        return args.getBoolean(ARGS_KEY_SHOW_MRU);
    }

    private MruPersister createMruPersister(Context context, boolean showMru) {
        if (showMru) {
            return SharedPreferencesMruPersister.newInstance(context);
        }
        return new DisabledMruPersister();
    }

    private void restoreShowingAllShareTargetsState(Bundle savedState) {
        if (savedState != null) {
            showingAllShareTargets = savedState.getBoolean(KEY_SHOWING_ALL_SHARE_TARGETS, false);
            if (showingAllShareTargets) {
                onShowMoreButtonClicked();
            }
        }
    }

    private void tryShowingContentOrSwitchToEmptyStateUi(ShareTargetsAdapter adapter) {
        if (adapter.isEmpty()) {
            onShowMoreButtonClicked();
            if (adapter.isEmpty()) {
                showEmptyStateUiOnly();
            }
        }
    }

    private Dialog createDialog(int themeResId, CharSequence title, View dialogView) {
        AlertDialog.Builder builder = createDialogBuilder(themeResId)
                .setCancelable(true)
                .setView(dialogView);

        if (!TextUtils.isEmpty(title)) {
            builder.setTitle(title);
        }

        return builder.create();
    }

    @SuppressLint("InflateParams")
    private ViewGroup createDialogView() {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        return (ViewGroup) inflater.inflate(R.layout.ps__share_dialog, null, false);
    }

    private void setupGridView(ViewGroup rootView, ListAdapter adapter) {
        GridView gridView = (GridView) rootView.findViewById(R.id.ps__dialog_grid);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(createOnListItemClickListener());
    }

    private AdapterView.OnItemClickListener createOnListItemClickListener() {
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startExplicitTargetShareIntentFor(position);
                dismiss();
            }
        };
    }

    private AlertDialog.Builder createDialogBuilder(int themeResId) {
        if (themeResId != 0) {
            return new AlertDialog.Builder(getActivity(), themeResId);
        } else {
            return new AlertDialog.Builder(getActivity());
        }
    }

    private ShareTargetsAdapter createShareTargetsAdapterFrom(Bundle args, Intent sharingIntent) {
        TargetApps targetApps = extractTargetAppsFrom(args);
        List<ResolveInfo> allShareTargets = getAvailableShareTargets(sharingIntent);
        List<ResolveInfo> priorityAppInfos = createPriorityResolveInfoList(targetApps, allShareTargets);
        List<ResolveInfo> otherAppInfos = createOtherAppsResolveInfoList(priorityAppInfos, allShareTargets);

        return ShareTargetsAdapter.newInstance(getActivity(), priorityAppInfos, otherAppInfos);
    }

    private TargetApps extractTargetAppsFrom(Bundle args) {
        if (args.containsKey(ARGS_KEY_TARGET_APPS)) {
            return args.getParcelable(ARGS_KEY_TARGET_APPS);
        } else {
            return TargetApps.NONE;
        }
    }

    private List<ResolveInfo> createPriorityResolveInfoList(TargetApps targetApps, List<ResolveInfo> allShareTargets) {
        List<ResolveInfo> resolveInfos = targetApps.extractTargetResolveInfosFrom(allShareTargets);

        if (showMru) {
            ResolveInfo mruResolveInfo = findMruResolveInfoIn(allShareTargets);
            if (mruResolveInfo != null) {
                ensureListContainsItem(resolveInfos, mruResolveInfo);
            }
        }

        return resolveInfos;
    }

    private List<ResolveInfo> createOtherAppsResolveInfoList(List<ResolveInfo> priorityAppInfos, List<ResolveInfo> allAppInfos) {
        List<ResolveInfo> otherAppsInfos = new ArrayList<ResolveInfo>(allAppInfos);
        for (ResolveInfo priorityAppInfo : priorityAppInfos) {
            otherAppsInfos.remove(priorityAppInfo);
        }
        return otherAppsInfos;
    }

    private ResolveInfo findMruResolveInfoIn(List<ResolveInfo> resolveInfos) {
        String mruPackageName = persister.getLastUsedTarget();
        for (ResolveInfo resolveInfo : resolveInfos) {
            String packageName = resolveInfo.activityInfo.packageName;
            if (packageName.equals(mruPackageName)) {
                return resolveInfo;
            }
        }
        return null;
    }

    private void ensureListContainsItem(List<ResolveInfo> resolveInfos, ResolveInfo targetResolveInfo) {
        if (!resolveInfos.contains(targetResolveInfo)) {
            resolveInfos.add(targetResolveInfo);
        }
    }

    private List<ResolveInfo> getAvailableShareTargets(Intent sharingIntent) {
        PackageManager packageManager = getActivity().getPackageManager();
        return packageManager.queryIntentActivities(sharingIntent, 0);
    }

    private void onShowMoreButtonClicked() {
        showAllShareTargets();
    }

    private void showAllShareTargets() {
        hideShowMoreButton();
        adapter.showAllApps();
        showingAllShareTargets = true;
    }

    private void showEmptyStateUiOnly() {
        hideShowMoreButton();
        hideGridView();

        getEmptyStateView().setVisibility(View.VISIBLE);
    }

    private void hideShowMoreButton() {
        View showMoreButton = getShowMoreButton();
        if (showMoreButton != null) {
            View buttonStrip = (View) showMoreButton.getParent();
            buttonStrip.setVisibility(View.GONE);
        }
    }

    private void hideGridView() {
        GridView gridView = getGridView();
        if (gridView != null) {
            gridView.setVisibility(View.GONE);
        }
    }

    private int getThemeResIdIfProvidedIn(Bundle args) {
        return args.getInt(ARGS_KEY_THEME_RES_ID, 0);
    }

    private CharSequence getTitleIfProvidedIn(Bundle args) {
        return args.getCharSequence(ARGS_KEY_TITLE, "");
    }

    private void startExplicitTargetShareIntentFor(int position) {
        Intent intent = wrapIntentWithExplicitTargetComponentForItem(position, sharingIntent);
        storeMruItemFrom(intent);
        intent = tweakIntentUsingListenerIfAnyIsSet(intent);
        startActivity(intent);
    }

    private void storeMruItemFrom(Intent intent) {
        persister.storeLastUsedTarget(intent.getComponent().getPackageName());
    }

    private Intent wrapIntentWithExplicitTargetComponentForItem(int position, Intent sharingIntent) {
        Intent intent = new Intent(sharingIntent);
        ActivityInfo activity = adapter.getItem(position).activityInfo;
        ComponentName name = new ComponentName(activity.applicationInfo.packageName, activity.name);
        intent.setComponent(name);

        return intent;
    }

    private Intent tweakIntentUsingListenerIfAnyIsSet(Intent intent) {
        if (listener == null) {
            return intent;
        }
        return listener.onPrepareSharingIntent(intent);
    }

    public void setOnPrepareSharingIntentListener(OnPrepareSharingIntentListener listener) {
        this.listener = listener;
    }

    @Override
    public void onStart() {
        super.onStart();
        // TODO show empty state?
        setupShowMoreButtonOnClickListener();
    }

    private void setupShowMoreButtonOnClickListener() {
        View showMoreButton = getShowMoreButton();
        if (showMoreButton != null) {
            showMoreButton.setOnClickListener(createOnShowMoreButtonClickListener());
        }
    }

    private View.OnClickListener createOnShowMoreButtonClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onShowMoreButtonClicked();
            }
        };
    }

    private View getShowMoreButton() {
        if (dialogView == null) {
            return null;
        }
        return dialogView.findViewById(R.id.ps__show_more_button);
    }

    private GridView getGridView() {
        if (dialogView == null) {
            return null;
        }
        return (GridView) dialogView.findViewById(R.id.ps__dialog_grid);
    }

    private View getEmptyStateView() {
        if (dialogView == null) {
            return null;
        }
        return dialogView.findViewById(android.R.id.empty);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_SHOWING_ALL_SHARE_TARGETS, showingAllShareTargets);
    }

    public static class Builder {

        private CharSequence title;
        private int themeResId;
        private TargetApps targetApps;
        private Intent sharingIntent;
        private OnPrepareSharingIntentListener listener;
        private boolean showMru;

        public Builder setTitle(CharSequence title) {
            this.title = title;
            this.showMru = true;
            return this;
        }

        public Builder setThemeResId(int themeResId) {
            this.themeResId = themeResId;
            return this;
        }

        public Builder setTargetApps(TargetApps targetApps) {
            this.targetApps = targetApps;
            return this;
        }

        public Builder setSharingIntent(Intent sharingIntent) {
            this.sharingIntent = sharingIntent;
            return this;
        }

        public Builder setOnPrepareSharingIntentListener(OnPrepareSharingIntentListener listener) {
            this.listener = listener;
            return this;
        }

        public Builder setShowMostRecentlyUsed(boolean showMostRecentlyUsed) {
            this.showMru = showMostRecentlyUsed;
            return this;
        }

        public PriorityShareDialog build() {
            PriorityShareDialog dialog = new PriorityShareDialog();

            Bundle args = new Bundle();
            if (!TextUtils.isEmpty(title)) {
                args.putCharSequence(ARGS_KEY_TITLE, title);
            }
            if (themeResId != 0) {
                args.putInt(ARGS_KEY_THEME_RES_ID, themeResId);
            }
            if (targetApps != null) {
                args.putParcelable(ARGS_KEY_TARGET_APPS, targetApps);
            }
            if (sharingIntent != null) {
                args.putParcelable(ARGS_KEY_SHARING_INTENT, sharingIntent);
            }
            args.putBoolean(ARGS_KEY_SHOW_MRU, showMru);

            dialog.setArguments(args);
            dialog.setOnPrepareSharingIntentListener(listener);
            return dialog;
        }

    }

}
