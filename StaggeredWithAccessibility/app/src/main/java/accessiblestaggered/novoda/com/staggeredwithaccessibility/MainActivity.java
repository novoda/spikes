package accessiblestaggered.novoda.com.staggeredwithaccessibility;

import android.app.Activity;
import android.os.Bundle;

import com.etsy.android.grid.StaggeredGridView;

public class MainActivity extends Activity {

    private StaggeredGridView gridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gridView = (StaggeredGridView) findViewById(R.id.gridview);
        gridView.setAdapter(new DummyAdapter(getApplicationContext(), getLayoutInflater()));
    }
}
