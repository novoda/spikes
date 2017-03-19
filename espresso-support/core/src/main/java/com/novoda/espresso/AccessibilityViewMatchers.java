package com.novoda.espresso;

import android.content.res.Resources;
import android.support.annotation.StringRes;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import static org.hamcrest.Matchers.is;

public class AccessibilityViewMatchers {

    public static Matcher<? super View> withUsageHintOnClick(@StringRes final int resourceId) {
        return new TypeSafeMatcher<View>() {

            private String resourceName = null;
            private String expectedText = null;

            @Override
            protected boolean matchesSafely(View view) {
                if (!view.isClickable()) {
                    return false;
                }

                if (null == this.expectedText) {
                    try {
                        expectedText = view.getResources().getString(resourceId);
                        resourceName = view.getResources().getResourceEntryName(resourceId);
                    } catch (Resources.NotFoundException ignored) {
                        // view could be from a context unaware of the resource id.
                    }
                }
                if (null != expectedText) {
                    AccessibilityNodeInfo accessibilityNodeInfo = view.createAccessibilityNodeInfo();
                    AccessibilityNodeInfo.AccessibilityAction clickAction = findAction(accessibilityNodeInfo, AccessibilityNodeInfo.ACTION_CLICK);

                    return expectedText.equals(clickAction.getLabel());
                } else {
                    return false;
                }
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("is clickable and has custom usage hint for ACTION_CLICK from resource id: ");
                description.appendValue(resourceId);
                if (null != this.resourceName) {
                    description.appendText("[");
                    description.appendText(resourceName);
                    description.appendText("]");
                }
                if (null != this.expectedText) {
                    description.appendText(" value: ");
                    description.appendText(expectedText);
                }
            }
        };
    }

    public static Matcher<? super View> withUsageHintOnClick(CharSequence text) {
        return withUsageHintOnClick(is(text));
    }

    public static Matcher<? super View> withUsageHintOnClick(final Matcher<? extends CharSequence> charSequenceMatcher) {
        return new TypeSafeMatcher<View>() {
            @Override
            protected boolean matchesSafely(View view) {
                if (!view.isClickable()) {
                    return false;
                }

                AccessibilityNodeInfo accessibilityNodeInfo = view.createAccessibilityNodeInfo();
                AccessibilityNodeInfo.AccessibilityAction clickAction = findAction(accessibilityNodeInfo, AccessibilityNodeInfo.ACTION_CLICK);

                return charSequenceMatcher.matches(clickAction.getLabel());
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("is clickable and has custom usage hint for ACTION_CLICK: ");
                charSequenceMatcher.describeTo(description);
            }
        };
    }

    public static Matcher<? super View> withUsageHintOnLongClick(@StringRes final int resourceId) {
        return new TypeSafeMatcher<View>() {

            private String resourceName = null;
            private String expectedText = null;

            @Override
            protected boolean matchesSafely(View view) {
                if (!view.isLongClickable()) {
                    return false;
                }

                if (null == this.expectedText) {
                    try {
                        expectedText = view.getResources().getString(resourceId);
                        resourceName = view.getResources().getResourceEntryName(resourceId);
                    } catch (Resources.NotFoundException ignored) {
                        // view could be from a context unaware of the resource id.
                    }
                }
                if (null != expectedText) {
                    AccessibilityNodeInfo accessibilityNodeInfo = view.createAccessibilityNodeInfo();
                    AccessibilityNodeInfo.AccessibilityAction clickAction = findAction(accessibilityNodeInfo, AccessibilityNodeInfo.ACTION_LONG_CLICK);

                    return expectedText.equals(clickAction.getLabel());
                } else {
                    return false;
                }
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("is long clickable and has custom usage hint for ACTION_LONG_CLICK from resource id: ");
                description.appendValue(resourceId);
                if (null != this.resourceName) {
                    description.appendText("[");
                    description.appendText(resourceName);
                    description.appendText("]");
                }
                if (null != this.expectedText) {
                    description.appendText(" value: ");
                    description.appendText(expectedText);
                }
            }
        };
    }

    public static Matcher<? super View> withUsageHintOnLongClick(CharSequence text) {
        return withUsageHintOnLongClick(is(text));
    }

    public static Matcher<? super View> withUsageHintOnLongClick(final Matcher<? extends CharSequence> charSequenceMatcher) {
        return new TypeSafeMatcher<View>() {
            @Override
            protected boolean matchesSafely(View view) {
                if (!view.isLongClickable()) {
                    return false;
                }

                AccessibilityNodeInfo accessibilityNodeInfo = view.createAccessibilityNodeInfo();
                AccessibilityNodeInfo.AccessibilityAction clickAction = findAction(accessibilityNodeInfo, AccessibilityNodeInfo.ACTION_LONG_CLICK);

                return charSequenceMatcher.matches(clickAction.getLabel());
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("is long clickable and has custom usage hint for ACTION_LONG_CLICK: ");
                charSequenceMatcher.describeTo(description);
            }
        };
    }

    public static AccessibilityNodeInfo.AccessibilityAction findAction(AccessibilityNodeInfo accessibilityNodeInfo, int actionId) {
        for (AccessibilityNodeInfo.AccessibilityAction accessibilityAction : accessibilityNodeInfo.getActionList()) {
            if (actionId == accessibilityAction.getId()) {
                return accessibilityAction;
            }
        }
        throw new AccessibilityActionNotFoundException(actionId);
    }

    public static class AccessibilityActionNotFoundException extends RuntimeException {

        public AccessibilityActionNotFoundException(int actionId) {
            super("Could not find AccessibilityAction with id: " + actionId);
        }
    }

}
