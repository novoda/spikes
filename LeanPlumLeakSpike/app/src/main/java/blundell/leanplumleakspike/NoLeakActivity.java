package blundell.leanplumleakspike;

import android.os.Bundle;

import com.leanplum.Leanplum;
import com.leanplum.activities.LeanplumActivity;
import com.leanplum.callbacks.StartCallback;

import java.lang.ref.WeakReference;

/**
 * I load the two big PNG's to make it obvious when the GC is allocating memory
 * <p/>
 * Load this activity - then rotate the device 3-10 times
 * <p/>
 * You can see memory footprint stays constant <i>free 14125K/26788K</i>
 * <p/>
 * GC_FOR_ALLOC freed 7685K, 48% free 14125K/26788K, paused 22ms, total 22ms
 * GC_FOR_ALLOC freed 7752K, 37% free 14131K/22080K, paused 22ms, total 22ms
 * GC_FOR_ALLOC freed 74K, 24% free 16786K/22080K, paused 18ms, total 19ms
 * GC_FOR_ALLOC freed 7685K, 48% free 14128K/26788K, paused 18ms, total 18ms
 * GC_FOR_ALLOC freed 7753K, 36% free 14131K/22080K, paused 20ms, total 20ms
 * GC_FOR_ALLOC freed 74K, 24% free 16787K/22080K, paused 16ms, total 17ms
 * GC_FOR_ALLOC freed 7685K, 48% free 14128K/26788K, paused 25ms, total 25ms
 */
public class NoLeakActivity extends LeanplumActivity {

    private static final String APP_ID = "iSWrh0faYbLaQAMYCrbWUG3HeKblsfHv0v4ntzqsKiE";
    private static final String DEV_KEY = "S1OiXsoaxBYQXVUMldEWRJDuuxz9aaQDsBj4m8RjHVo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Show a loading view
        setContentView(R.layout.activity_loading);
        // Setup leanplum
        Leanplum.setAppIdForDevelopmentMode(APP_ID, DEV_KEY);
        // Ask lean plum to go fetch the latest AB variables
        Leanplum.start(this);
        // Add a response handler to be told when the AB variable has been updated for the first time
        // When they are updated consequent times we won't get a callback
        // and if they variable was updated before we set this callback we get called back instantly
        Leanplum.addStartResponseHandler(WeakStartCallback.newInstance(this));
    }

    // Passing a static class as reference that holds a weak reference to our outer class (Activity) into Leanplum
    // Means this reference does not count to the Garbage collector and therefore can be
    // cleared NO MEMORY LEAK
    // This is what really should be done INSIDE the library
    private static final class WeakStartCallback extends StartCallback {

        private final WeakReference<NoLeakActivity> weakNoLeakActivity;

        public static WeakStartCallback newInstance(NoLeakActivity noLeakActivity) {
            WeakReference<NoLeakActivity> weakNoLeakActivity = new WeakReference<NoLeakActivity>(noLeakActivity);
            return new WeakStartCallback(weakNoLeakActivity);
        }

        private WeakStartCallback(WeakReference<NoLeakActivity> weakNoLeakActivity) {
            this.weakNoLeakActivity = weakNoLeakActivity;
        }

        @Override
        public void onResponse(boolean b) {
            NoLeakActivity noLeakActivity = weakNoLeakActivity.get();
            if (noLeakActivity == null) {
                return;
            }
            noLeakActivity.whenAbLoadedSetContent();
        }
    }

    public void whenAbLoadedSetContent() {
        setContentView(R.layout.activity_my);
    }

}
