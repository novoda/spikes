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
        setContentView(R.layout.activity_loading);
        Leanplum.setAppIdForDevelopmentMode(APP_ID, DEV_KEY);
        Leanplum.start(this);
        Leanplum.addStartResponseHandler(startCallback);
    }

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
