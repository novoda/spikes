Utility classes for implementing accessible apps

## AccessibilityServices
- Check if TalkBack (or other spoken feedback accessibility service) is enabled
- Reports as enabled even if TalkBack is suspended

```java
AccessibilityServices services = AccessibilityServices.newInstance(context);
services.isSpokenFeedbackEnabled();
```

## Custom accessibility actions
- a wrapper around View actions to facilitate actions via dialog or TalkBack local gestures menu

First create `Actions`. It's necessary to give each `Action` a unique resource ID (required by [`AccessibilityActionCompat`](http://developer.android.com/reference/android/support/v4/view/accessibility/AccessibilityNodeInfoCompat.AccessibilityActionCompat.html) see `ActionsAccessibilityDelegate`), and a display label.

```java
Actions actions = // ...
AccessibilityDelegateCompat delegate = new ActionsAccessibilityDelegate(getResources(), actions);
ViewCompat.setAccessibilityDelegate(this, delegate);

// ...

// if TalkBack is enabled, display dialog on click
private void showAlertDialogFor(Actions actions) {
    new ActionsAlertDialogCreator(getContext(), R.string.tweet_actions_title, actions)
            .create()
            .show();
}
```
