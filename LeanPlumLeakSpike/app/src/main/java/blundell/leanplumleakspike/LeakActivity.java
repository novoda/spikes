package blundell.leanplumleakspike;

import android.os.Bundle;

import com.leanplum.Leanplum;
import com.leanplum.activities.LeanplumActivity;
import com.leanplum.callbacks.StartCallback;

/**
 * I load the two big PNG's to make it obvious when the GC is allocating memory
 * <p/>
 * Load this activity - then rotate the device 3-10 times
 * <p/>
 * You can see memory footprint is incremental <i>free 69584K/73868K</i>
 * <p/>
 * GC_FOR_ALLOC freed 2904K, 25% free 23706K/31496K, paused 24ms, total 24ms
 * GC_FOR_ALLOC freed 52K, 17% free 26384K/31496K, paused 19ms, total 19ms
 * GC_FOR_ALLOC freed 52K, 11% free 40782K/45620K, paused 22ms, total 22ms
 * GC_FOR_ALLOC freed 53K, 10% free 45582K/50328K, paused 20ms, total 20ms
 * GC_FOR_ALLOC freed 86K, 7% free 59984K/64452K, paused 21ms, total 21ms
 * GC_FOR_ALLOC freed 2907K, 11% free 62103K/69160K, paused 34ms, total 34ms
 * GC_FOR_ALLOC freed 52K, 7% free 64781K/69160K, paused 23ms, total 23ms
 * GC_FOR_ALLOC freed 2830K, 10% free 66905K/73868K, paused 33ms, total 34ms
 * GC_FOR_ALLOC freed 122K, 6% free 69584K/73868K, paused 36ms, total 36ms
 */
public class LeakActivity extends LeanplumActivity {

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
        Leanplum.addStartResponseHandler(startCallback);
    }

    // Passing this annonymous class as reference leaks our outer class (Activity) into Leanplum
    // Leanplum seems to be holding a list of these and therefore all Activities cannot be
    // cleared by the garbage collector MEMORY LEAK
    private final StartCallback startCallback = new StartCallback() {
        @Override
        public void onResponse(boolean b) {
            whenAbLoadedSetContent();
        }
    };

    public void whenAbLoadedSetContent() {
        setContentView(R.layout.activity_my);
    }

}
