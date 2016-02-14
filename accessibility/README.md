Utility classes for implementing accessible apps

## AccessibilityServices
- Check if TalkBack (or other spoken feedback accessibility service) is enabled
- Reports as enabled even if TalkBack is suspended

```java
AccessibilityServices services = AccessibilityServices.newInstance(context);
services.isSpokenFeedbackEnabled();
```

