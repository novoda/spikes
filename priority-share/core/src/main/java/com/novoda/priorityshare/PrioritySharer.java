package com.novoda.priorityshare;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

/**
 * Implements the logic to perform a <i>priority share</i> action,
 * that is, presenting the user with a set of predefined apps
 * (as specified in a {@link com.novoda.priorityshare.TargetApps})
 * to share to, as a first "quick dial" experience, and offering a
 * "show more" to allow the user to choose other, non-prioritised
 * apps to share to.
 *
 * @see com.novoda.priorityshare.TargetApps
 */
public class PrioritySharer {

    private CharSequence title;
    private TargetApps targetApps;
    private String mimeType;
    private Uri dataUri;
    private CharSequence subject;
    private CharSequence text;
    private OnPrepareSharingIntentListener listener;
    private boolean showMostRecentlyUsed;

    PrioritySharer() {
    }

    public void showShareDialog(Activity activity) {
        Intent sharingIntent = createSharingIntent();
        FragmentManager fragmentManager = activity.getFragmentManager();
        showPriorityShareDialog(targetApps, sharingIntent, fragmentManager);
    }

    private Intent createSharingIntent() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        if (!TextUtils.isEmpty(title)) {
            intent.putExtra(Intent.EXTRA_TITLE, title);
        }
        if (!TextUtils.isEmpty(subject)) {
            intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        }
        if (!TextUtils.isEmpty(text)) {
            intent.putExtra(Intent.EXTRA_TEXT, text);
        }
        if (!TextUtils.isEmpty(mimeType)) {
            intent.setType(mimeType);
        }
        if (dataUri != null) {
            intent.setData(dataUri);
        }
        return intent;
    }

    private void showPriorityShareDialog(TargetApps targetApps, Intent sharingIntent, FragmentManager fragmentManager) {
        PriorityShareDialog dialog = new PriorityShareDialog.Builder()
                .setTitle(title)
                .setTargetApps(targetApps)
                .setSharingIntent(sharingIntent)
                .setOnPrepareSharingIntentListener(listener)
                .setShowMostRecentlyUsed(showMostRecentlyUsed)
                .build();

        dialog.show(fragmentManager, PriorityShareDialog.TAG_PRIORITY_SHARE_DIALOG);
    }

    public static class Builder {

        public static final String MIME_TYPE_TEXT_PLAIN = "text/plain";

        private CharSequence title;
        private TargetApps targetApps;
        private String mimeType;
        private Uri dataUri;
        private CharSequence subject;
        private CharSequence text;
        private OnPrepareSharingIntentListener listener;
        private boolean showMostRecentlyUsed;

        public Builder() {
            this.mimeType = MIME_TYPE_TEXT_PLAIN;
            this.targetApps = TargetApps.NONE;
            this.listener = new SimpleOnPrepareSharingIntentListener();
            this.showMostRecentlyUsed = true;
        }

        public Builder setTitle(CharSequence title) {
            this.title = title;
            return this;
        }

        public Builder setTargets(TargetApps targetApps) {
            this.targetApps = targetApps;
            return this;
        }

        public Builder setMimeType(String mimeType) {
            this.mimeType = mimeType;
            return this;
        }

        public Builder setDataUri(Uri dataUri) {
            this.dataUri = dataUri;
            return this;
        }

        public Builder setSubject(CharSequence subject) {
            this.subject = subject;
            return this;
        }

        public Builder setText(CharSequence text) {
            this.text = text;
            return this;
        }

        public Builder setOnPrepareSharingIntentListener(OnPrepareSharingIntentListener listener) {
            this.listener = listener;
            return this;
        }

        public Builder showMostRecentlyUsed(boolean showMostRecentlyUsed) {
            this.showMostRecentlyUsed = showMostRecentlyUsed;
            return this;
        }

        private PrioritySharer build() {
            PrioritySharer prioritySharer = new PrioritySharer();
            prioritySharer.title = title;
            prioritySharer.targetApps = targetApps;
            prioritySharer.mimeType = mimeType;
            prioritySharer.dataUri = dataUri;
            prioritySharer.subject = subject;
            prioritySharer.text = text;
            prioritySharer.listener = listener;
            prioritySharer.showMostRecentlyUsed = showMostRecentlyUsed;

            return prioritySharer;
        }

        public void show(Activity activity) {
            PrioritySharer prioritySharer = build();
            prioritySharer.showShareDialog(activity);
        }
    }

}
