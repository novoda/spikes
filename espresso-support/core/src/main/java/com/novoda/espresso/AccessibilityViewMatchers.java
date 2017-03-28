package com.novoda.espresso;

import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.annotation.StringRes;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import static org.hamcrest.Matchers.is;

public class AccessibilityViewMatchers {

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    public static Matcher<? super View> withUsageHintOnClick(@StringRes final int resourceId) {
        return new TypeSafeMatcher<View>() {

            private String resourceName;
            private String expectedText;

            @Override
            protected boolean matchesSafely(View view) {
                if (!view.isClickable()) {
                    return false;
                }

                expectedText = view.getResources().getString(resourceId);
                resourceName = view.getResources().getResourceEntryName(resourceId);

                if (expectedText == null) {
                    return false;
                }

                AccessibilityNodeInfo.AccessibilityAction clickAction = findAction(view, AccessibilityNodeInfo.ACTION_CLICK);
                return expectedText.equals(clickAction.getLabel());
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("is clickable and has custom usage hint for ACTION_CLICK from resource id: ").appendValue(resourceId);
                appendResourceNameAndExpectedTextToDescription(description, resourceName, expectedText);
            }
        };
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    public static Matcher<? super View> withUsageHintOnClick(CharSequence text) {
        return withUsageHintOnClick(is(text));
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    public static Matcher<? super View> withUsageHintOnClick(final Matcher<? extends CharSequence> charSequenceMatcher) {
        return new TypeSafeMatcher<View>() {
            @Override
            protected boolean matchesSafely(View view) {
                if (!view.isClickable()) {
                    return false;
                }
                AccessibilityNodeInfo.AccessibilityAction clickAction = findAction(view, AccessibilityNodeInfo.ACTION_CLICK);
                return charSequenceMatcher.matches(clickAction.getLabel());
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("is clickable and has custom usage hint for ACTION_CLICK: ");
                charSequenceMatcher.describeTo(description);
            }
        };
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    public static Matcher<? super View> withUsageHintOnLongClick(@StringRes final int resourceId) {
        return new TypeSafeMatcher<View>() {

            private String resourceName;
            private String expectedText;

            @Override
            protected boolean matchesSafely(View view) {
                if (!view.isLongClickable()) {
                    return false;
                }

                expectedText = view.getResources().getString(resourceId);
                resourceName = view.getResources().getResourceEntryName(resourceId);

                if (expectedText == null) {
                    return false;
                }

                AccessibilityNodeInfo.AccessibilityAction clickAction = findAction(view, AccessibilityNodeInfo.ACTION_LONG_CLICK);
                return expectedText.equals(clickAction.getLabel());
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("is long clickable and has custom usage hint for ACTION_LONG_CLICK from resource id: ").appendValue(resourceId);
                appendResourceNameAndExpectedTextToDescription(description, resourceName, expectedText);
            }
        };
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    public static Matcher<? super View> withUsageHintOnLongClick(CharSequence text) {
        return withUsageHintOnLongClick(is(text));
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    public static Matcher<? super View> withUsageHintOnLongClick(final Matcher<? extends CharSequence> charSequenceMatcher) {
        return new TypeSafeMatcher<View>() {
            @Override
            protected boolean matchesSafely(View view) {
                if (!view.isLongClickable()) {
                    return false;
                }
                AccessibilityNodeInfo.AccessibilityAction clickAction = findAction(view, AccessibilityNodeInfo.ACTION_LONG_CLICK);
                return charSequenceMatcher.matches(clickAction.getLabel());
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("is long clickable and has custom usage hint for ACTION_LONG_CLICK: ");
                charSequenceMatcher.describeTo(description);
            }
        };
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    public static AccessibilityNodeInfo.AccessibilityAction findAction(View view, int actionId) {
        AccessibilityNodeInfo accessibilityNodeInfo = view.createAccessibilityNodeInfo();
        for (AccessibilityNodeInfo.AccessibilityAction accessibilityAction : accessibilityNodeInfo.getActionList()) {
            if (actionId == accessibilityAction.getId()) {
                return accessibilityAction;
            }
        }
        throw new AccessibilityActionNotFoundException(actionId);
    }

    public static void appendResourceNameAndExpectedTextToDescription(Description description, @Nullable String resourceName, @Nullable String expectedText) {
        if (resourceName != null) {
            description.appendText("[").appendText(resourceName).appendText("]");
        }
        if (expectedText != null) {
            description.appendText(" value: ").appendText(expectedText);
        }
    }

    public static class AccessibilityActionNotFoundException extends RuntimeException {

        public AccessibilityActionNotFoundException(int actionId) {
            super("Could not find AccessibilityAction with id: " + actionId);
        }

    }

}
