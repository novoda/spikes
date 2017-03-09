package com.novoda.espresso;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class ViewActivity extends Activity {

    public static final String EXTRA_LAYOUT_ID = ViewActivity.class.getName() + ".EXTRA_LAYOUT_ID";

    private View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int layout = getIntent().getIntExtra(EXTRA_LAYOUT_ID, 0);

        view = getLayoutInflater().inflate(layout, null, false);
        ViewGroup.LayoutParams params = createMatchParentParams();

        setContentView(view, params);
    }

    private ViewGroup.LayoutParams createMatchParentParams() {
        return new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT);
    }

    public View getView() {
        return view;
    }

}
