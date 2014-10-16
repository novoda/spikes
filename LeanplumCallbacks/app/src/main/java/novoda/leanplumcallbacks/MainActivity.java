package novoda.leanplumcallbacks;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.leanplum.Leanplum;
import com.leanplum.LeanplumActivityHelper;
import com.leanplum.annotations.Parser;

public class MainActivity extends Activity {
    private LeanplumActivityHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TextView abText = (TextView) findViewById(R.id.ab_text);
        Leanplum.setAppIdForDevelopmentMode(BuildConfig.LEAN_PLUM_APP_ID, BuildConfig.LEAN_PLUM_DEV_KEY);
        Leanplum.enableVerboseLoggingInDevelopmentMode();

        //tell Leanplum to inject the variables into this class
        Parser.parseVariablesForClasses(AyBe.class);
        Leanplum.start(this);
        final AyBe ayBe = new AyBe();

        //attach the callback
        ayBe.start(new AbTestingParamsLoadedCallback() {
            @Override
            public void onParamsLoaded() {
                abText.setText("Variable from Leanplum: " + ayBe.getAbTesting());
            }
        });
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        getHelper().setContentView(layoutResID);
    }

    private LeanplumActivityHelper getHelper() {
        if (helper == null) {
            helper = new LeanplumActivityHelper(this);
        }
        return helper;
    }

    @Override
    protected void onResume() {
        super.onResume();
        getHelper().onResume();
    }

    @Override
    public Resources getResources() {
        return getHelper().getLeanplumResources(super.getResources());
    }

    @Override
    protected void onPause() {
        super.onPause();
        getHelper().onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        getHelper().onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
