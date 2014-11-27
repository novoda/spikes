package com.novoda.priorityshare.composer;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

/**
 * A {@link com.novoda.priorityshare.composer.MessageComposer} that enforces a
 * maximum composed text length.
 * <p/>
 * This class assumes URLs will <i>NOT</i> be shortened. If a different behaviour
 * is required, users can subclass and override {@link #getShortenedUrlLengthFor(String)}
 * to provide any custom shortened URL length calculation logic. Please note that
 * this class <i>NOT</i> perform the shortening itself, as this is usually done by
 * the sharing targets themselves when submitting the post. It will only do the
 * length limiting based on the final, shortened URL length.
 * <p/>
 * The composed text will have the following format:
 * <code>[prefix] [subject] [url] [suffix]</code>
 * Where the <code>[subject]</code> field will be ellipsized as needed to fit any
 * characters allowance left from the other fields.
 */
public class MaxLengthMessageComposer implements MessageComposer {

    private String suffix;
    private String prefix;
    private int maxLength;

    public MaxLengthMessageComposer(int maxLength) {
        this.maxLength = maxLength;
    }

    public MaxLengthMessageComposer setSuffix(String suffix) {
        this.suffix = safeTrim(suffix);
        return this;
    }

    public MaxLengthMessageComposer setPrefix(String prefix) {
        this.prefix = safeTrim(prefix);
        return this;
    }

    private static String safeTrim(String text) {
        return text != null ? text.trim() : "";
    }

    @Override
    public Bundle composeToBundle(String subject, String url) {
        Bundle bundle = new Bundle();
        bundle.putString(Intent.EXTRA_TEXT, composeText(subject, url));
        return bundle;
    }

    private String composeText(String subject, String url) {
        int availableLength = maxLength;
        StringBuilder stringBuilder = new StringBuilder(maxLength);

        int prefixAndSuffixLength = computeAppendedLengthIncludingSeparatorSpaces(prefix, suffix);
        int urlLength = computeUrlLengthIncludingSeparatorSpace(url);
        availableLength -= prefixAndSuffixLength;
        availableLength -= urlLength;

        appendWithTrailingSpaceIfNotNullOrEmpty(prefix, stringBuilder);
        String trimmedSubject = ellipsizeTextIfExceedsMaxLength(subject, availableLength);
        appendWithTrailingSpaceIfNotNullOrEmpty(trimmedSubject, stringBuilder);
        appendWithTrailingSpaceIfNotNullOrEmpty(url, stringBuilder);
        appendWithTrailingSpaceIfNotNullOrEmpty(suffix, stringBuilder);

        return stringBuilder.toString().trim();
    }

    private int computeUrlLengthIncludingSeparatorSpace(String uri) {
        if (TextUtils.isEmpty(uri)) {
            return 0;
        }

        return getShortenedUrlLengthFor(uri) + 1;
    }

    protected int getShortenedUrlLengthFor(String uri) {
        return uri.length();
    }

    private int computeAppendedLengthIncludingSeparatorSpaces(String... stringsToAppend) {
        int length = 0;
        for (String string : stringsToAppend) {
            if (!TextUtils.isEmpty(string)) {
                length += string.length() + 1;
            }
        }

        return length;
    }

    private void appendWithTrailingSpaceIfNotNullOrEmpty(String string, StringBuilder stringBuilder) {
        if (!TextUtils.isEmpty(string)) {
            stringBuilder.append(string).append(" ");
        }
    }

    private String ellipsizeTextIfExceedsMaxLength(String text, int maxLength) {
        if (TextUtils.isEmpty(text)) {
            return null;
        }

        if (text.length() > maxLength) {
            text = text.substring(0, maxLength - 1) + "…";
        }
        return text;
    }

}
