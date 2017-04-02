package com.novoda.pianohero;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

public class SimpleEndToEndActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_end_to_end);

        SimpleNotesOutputView outputView = (SimpleNotesOutputView) findViewById(R.id.simple_notes_output_view);
        outputView.display(new SongSequenceFactory().maryHadALittleLamb());
    }
}
