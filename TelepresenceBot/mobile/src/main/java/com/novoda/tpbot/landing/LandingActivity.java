package com.novoda.tpbot.landing;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.novoda.notils.caster.Views;
import com.novoda.tpbot.FeatureSelectionController;
import com.novoda.tpbot.R;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import dagger.android.support.DaggerAppCompatActivity;

public class LandingActivity extends DaggerAppCompatActivity implements LandingView {

    @Inject
    FeatureSelectionController<Menu, MenuItem> featureSelectionController;
    @Inject
    LandingPresenter presenter;

    private View humanSelectionView;
    private View botSelectionView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        humanSelectionView = Views.findById(this, R.id.human_selection);
        botSelectionView = Views.findById(this, R.id.bot_selection);

    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.startPresenting();
    }

    @Override
    public void update(final LandingPresenter.Actions actions) {
        humanSelectionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actions.onHumanSelected();
            }
        });

        botSelectionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actions.onBotSelected();
            }
        });
    }

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

    @Override
    protected void onStop() {
        presenter.stopPresenting();
        super.onStop();
    }

}
