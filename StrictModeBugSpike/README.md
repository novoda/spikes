The enclosed application highlights a bug with StrictMode.

Have an application that enables strict mode with penalty death.

```java
StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .penaltyDeath()
                .build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .penaltyDeath()
                .build());
```
                
Create a blank activity that does nothing.
                
Rotate the device twice.
                
Crash:

```bash
-26 11:55:10.747    6558-6558/com.what.strictmodebugspike.app W/System.err﹕ StrictMode VmPolicy violation with POLICY_DEATH; shutting down.
02-26 11:56:35.067    6651-6651/com.what.strictmodebugspike.app D/StrictModeFail﹕ Rotate the device twice I will crash.
02-26 11:56:36.317    6651-6651/com.what.strictmodebugspike.app D/dalvikvm﹕ GC_EXPLICIT freed 124K, 3% free 7693K/7884K, paused 2ms+2ms, total 59ms
02-26 11:56:36.327    6651-6651/com.what.strictmodebugspike.app E/StrictMode﹕ class com.what.strictmodebugspike.app.MainActivity; instances=2; limit=1
    android.os.StrictMode$InstanceCountViolation: class com.what.strictmodebugspike.app.MainActivity; instances=2; limit=1
            at android.os.StrictMode.setClassInstanceLimit(StrictMode.java:1)
```
