package com.novoda.androidskeleton;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;

import java.util.Arrays;

public class MyActivity extends AppCompatActivity {

    private PeekableMoreThingsView peekableMoreThingsView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        peekableMoreThingsView = (PeekableMoreThingsView) findViewById(R.id.peekable_more_things);
        peekableMoreThingsView.bind(createMoreThings());
    }

    private MoreThings createMoreThings() {
        return new MoreThings(
                Arrays.asList(
                        new Row("Things next", Arrays.asList(new Item("Red", Color.RED), new Item("Red2", Color.RED), new Item("Red3", Color.RED))),
                        new Row("Blue things", Arrays.asList(new Item("Blue", Color.BLUE), new Item("Blue2", Color.BLUE), new Item("Blue3", Color.BLUE), new Item("Blue4", Color.BLUE), new Item("Blue5", Color.BLUE))),
                        new Row("Some green things", Arrays.asList(new Item("Green", Color.GREEN), new Item("Green2", Color.GREEN), new Item("Green3", Color.GREEN))),
                        new Row("Yellow things", Arrays.asList(new Item("Yellow", Color.YELLOW), new Item("Yellow2", Color.YELLOW), new Item("Yellow3", Color.YELLOW), new Item("Yellow4", Color.YELLOW), new Item("Yellow5", Color.YELLOW)))
                )
        );
    }

    public void showMoreThings(View button) {
        peekableMoreThingsView.showPeekView();
    }
}
