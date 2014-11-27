package com.novoda.priorityshare;

import android.content.ComponentName;
import android.content.Intent;

import com.novoda.priorityshare.composer.MessageComposer;
import com.novoda.priorityshare.composer.TwitterMessageComposer;

import java.util.Collection;

public class TwitterOnPrepareSharingIntentListener implements OnPrepareSharingIntentListener {

    private String prefix;
    private String url;
    private String suffix;

    public TwitterOnPrepareSharingIntentListener(String prefix, String url, String suffix) {
        this.prefix = prefix;
        this.url = url;
        this.suffix = suffix;
    }

    @Override
    public Intent onPrepareSharingIntent(Intent intent) {
        if (isIntentTargetATwitterClient(intent)) {
            CharSequence originalSubject = intent.getCharSequenceExtra(Intent.EXTRA_SUBJECT);
            String subject = originalSubject != null ? originalSubject.toString() : null;

            MessageComposer composer = createComposer();
            intent.putExtras(composer.composeToBundle(subject, url));
        }

        return intent;
    }

    private boolean isIntentTargetATwitterClient(Intent intent) {
        ComponentName component = intent.getComponent();
        if (component != null) {
            String targetPackageName = component.getPackageName();
            return isStringEqualToAnyInArray(targetPackageName, TargetApps.TWITTER.getTargetPackages());
        }
        return false;
    }

    private boolean isStringEqualToAnyInArray(String originalString, Collection<String> stringsArray) {
        for (String arrayItem : stringsArray) {
            if (originalString.equals(arrayItem)) {
                return true;
            }
        }
        return false;
    }

    private MessageComposer createComposer() {
        return new TwitterMessageComposer()
                .setPrefix(prefix)
                .setSuffix(suffix);
    }

}
