package com.novoda.tpbot.landing;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.novoda.tpbot.R;
import com.novoda.tpbot.bot.BotActivity;
import com.novoda.tpbot.feature_selection.FeatureSelectionController;
import com.novoda.tpbot.landing.features.LandingMenuFeatureSelectionController;
import com.novoda.tpbot.human.HumanActivity;

public class LandingActivity extends AppCompatActivity {

    private FeatureSelectionController<Menu, MenuItem> featureSelectionController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        featureSelectionController = LandingMenuFeatureSelectionController.createFrom(this);

        setContentView(R.layout.activity_landing);

        View humanSelection = findViewById(R.id.human_selection);
        View botSelection = findViewById(R.id.bot_selection);

        humanSelection.setOnClickListener(onHumanSelectionListener);
        botSelection.setOnClickListener(onBotSelectionListener);
    }

    private final View.OnClickListener onHumanSelectionListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getApplicationContext(), HumanActivity.class);
            startActivity(intent);
        }
    };

    private final View.OnClickListener onBotSelectionListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getApplicationContext(), BotActivity.class);
            startActivity(intent);
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        featureSelectionController.attachFeatureSelectionTo(menu);
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
