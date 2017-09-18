package com.novoda.tpbot.landing;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.novoda.tpbot.FeatureSelectionController;
import com.novoda.tpbot.R;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

public class LandingActivity extends AppCompatActivity {

    @Inject
    FeatureSelectionController<Menu, MenuItem> featureSelectionController;
    @Inject
    Navigator navigator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidInjection.inject(this);

        setContentView(R.layout.activity_landing);

        View humanSelection = findViewById(R.id.human_selection);
        View botSelection = findViewById(R.id.bot_selection);

        humanSelection.setOnClickListener(onHumanSelectionListener);
        botSelection.setOnClickListener(onBotSelectionListener);
    }

    private final View.OnClickListener onHumanSelectionListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            navigator.toHuman();
        }
    };

    private final View.OnClickListener onBotSelectionListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            navigator.toBot();
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        featureSelectionController.attachFeaturesTo(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (featureSelectionController.contains(item)) {
            featureSelectionController.handleFeatureToggle(item);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

}
