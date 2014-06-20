package blundell.leanplumleakspike;

import android.os.Bundle;

import com.leanplum.Leanplum;
import com.leanplum.activities.LeanplumActivity;
import com.leanplum.callbacks.StartCallback;

public class MyActivity extends LeanplumActivity {

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
