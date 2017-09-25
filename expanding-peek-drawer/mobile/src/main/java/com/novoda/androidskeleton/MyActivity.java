package com.novoda.androidskeleton;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.Arrays;

public class MyActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        PeekableMoreThingsView view = (PeekableMoreThingsView) findViewById(R.id.peekable_more_things);
        MoreThings moreThings = createMoreThings();
        view.bind(moreThings);
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
}
