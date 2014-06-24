StaggeredGridViewWithAccessibility

This example shows that the StaggeredGridView breaks the UiAutomator parser.

What are we doing:

Using StaggeredGridView to display items with more than one view and trying 
to get UiAutomator running on it. 
Use Android Device Monitor to have UiAutomator tool to dump View Hierachy. 

What is the problem:

UiAutomator will be able to find all layouts and identify their children during the very first pass.
But after a couple of rotations of the screen, UiAutomator starts to have problem detecting the children
of the layouts and it is even sometimes completely unable to dump anything at all. 

Assumptions:
Since UiAutomator is relying on the Accessibility framework of Android to find layouts and their children, it 
looks like the StaggeredGridView is breaking that and therefore makes it impossible for UiAutomator to run.
When UiAutomator fails to dump anything, the following stack trace is seen on the device:

D/AndroidRuntime( 1348): Calling main entry com.android.commands.uiautomator.Launcher
D/AndroidRuntime( 1348): Shutting down VM
W/dalvikvm( 1348): threadid=1: thread exiting with uncaught exception (group=0xa4d65b20)
E/JavaBinder( 1348): Unknown binder error code. 0xfffffff7
E/ServiceManager( 1348): error in getService
E/ServiceManager( 1348): android.os.RemoteException: Unknown binder error code. 0xfffffff7
E/ServiceManager( 1348): 	at android.os.BinderProxy.transact(Native Method)
E/ServiceManager( 1348): 	at android.os.ServiceManagerProxy.getService(ServiceManagerNative.java:123)
E/ServiceManager( 1348): 	at android.os.ServiceManager.getService(ServiceManager.java:55)
E/ServiceManager( 1348): 	at android.app.ActivityManagerNative$1.create(ActivityManagerNative.java:2042)
E/ServiceManager( 1348): 	at android.app.ActivityManagerNative$1.create(ActivityManagerNative.java:2040)
E/ServiceManager( 1348): 	at android.util.Singleton.get(Singleton.java:34)
E/ServiceManager( 1348): 	at android.app.ActivityManagerNative.getDefault(ActivityManagerNative.java:76)
E/ServiceManager( 1348): 	at com.android.internal.os.RuntimeInit$UncaughtHandler.uncaughtException(RuntimeInit.java:84)
E/ServiceManager( 1348): 	at java.lang.ThreadGroup.uncaughtException(ThreadGroup.java:693)
E/ServiceManager( 1348): 	at java.lang.ThreadGroup.uncaughtException(ThreadGroup.java:690)
E/ServiceManager( 1348): 	at dalvik.system.NativeStart.main(Native Method)
E/AndroidRuntime( 1348): *** FATAL EXCEPTION IN SYSTEM PROCESS: main
E/AndroidRuntime( 1348): java.lang.NullPointerException
E/AndroidRuntime( 1348): 	at com.android.uiautomator.core.AccessibilityNodeInfoDumper.childNafCheck(AccessibilityNodeInfoDumper.java:200)
E/AndroidRuntime( 1348): 	at com.android.uiautomator.core.AccessibilityNodeInfoDumper.childNafCheck(AccessibilityNodeInfoDumper.java:204)
E/AndroidRuntime( 1348): 	at com.android.uiautomator.core.AccessibilityNodeInfoDumper.nafCheck(AccessibilityNodeInfoDumper.java:180)
E/AndroidRuntime( 1348): 	at com.android.uiautomator.core.AccessibilityNodeInfoDumper.dumpNodeRec(AccessibilityNodeInfoDumper.java:104)
E/AndroidRuntime( 1348): 	at com.android.uiautomator.core.AccessibilityNodeInfoDumper.dumpNodeRec(AccessibilityNodeInfoDumper.java:129)
E/AndroidRuntime( 1348): 	at com.android.uiautomator.core.AccessibilityNodeInfoDumper.dumpNodeRec(AccessibilityNodeInfoDumper.java:129)
E/AndroidRuntime( 1348): 	at com.android.uiautomator.core.AccessibilityNodeInfoDumper.dumpNodeRec(AccessibilityNodeInfoDumper.java:129)
E/AndroidRuntime( 1348): 	at com.android.uiautomator.core.AccessibilityNodeInfoDumper.dumpNodeRec(AccessibilityNodeInfoDumper.java:129)
E/AndroidRuntime( 1348): 	at com.android.uiautomator.core.AccessibilityNodeInfoDumper.dumpWindowToFile(AccessibilityNodeInfoDumper.java:89)
E/AndroidRuntime( 1348): 	at com.android.commands.uiautomator.DumpCommand.run(DumpCommand.java:99)
E/AndroidRuntime( 1348): 	at com.android.commands.uiautomator.Launcher.main(Launcher.java:83)
E/AndroidRuntime( 1348): 	at com.android.internal.os.RuntimeInit.nativeFinishInit(Native Method)
E/AndroidRuntime( 1348): 	at com.android.internal.os.RuntimeInit.main(RuntimeInit.java:243)
E/AndroidRuntime( 1348): 	at dalvik.system.NativeStart.main(Native Method)
I/Process ( 1348): Sending signal. PID: 1348 SIG: 9
E/AndroidRuntime( 1348): Error reporting crash
E/AndroidRuntime( 1348): java.lang.NullPointerException
E/AndroidRuntime( 1348): 	at com.android.internal.os.RuntimeInit$UncaughtHandler.uncaughtException(RuntimeInit.java:84)
E/AndroidRuntime( 1348): 	at java.lang.ThreadGroup.uncaughtException(ThreadGroup.java:693)
E/AndroidRuntime( 1348): 	at java.lang.ThreadGroup.uncaughtException(ThreadGroup.java:690)
E/AndroidRuntime( 1348): 	at dalvik.system.NativeStart.main(Native Method)

Pointers:
StaggeredGridView being a custom view, adding the missing Accessibility stuff could/should be the solution. 
https://developer.android.com/guide/topics/ui/accessibility/apps.html






