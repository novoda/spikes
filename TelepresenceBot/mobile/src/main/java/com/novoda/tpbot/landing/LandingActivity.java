package com.novoda.tpbot.landing;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.novoda.notils.caster.Views;
import com.novoda.tpbot.FeatureSelectionController;
import com.novoda.tpbot.R;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

public class LandingActivity extends AppCompatActivity implements LandingView {

    @Inject
    FeatureSelectionController<Menu, MenuItem> featureSelectionController;
    @Inject
    LandingPresenter presenter;

    private View humanSelectionView;
    private View botSelectionView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        humanSelectionView = Views.findById(this, R.id.human_selection);
        botSelectionView = Views.findById(this, R.id.bot_selection);

        AndroidInjection.inject(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.startPresenting();
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

    @Override
    public void setSelectionListener(final LandingPresenter.Listener listener) {
        humanSelectionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onHumanSelected();
            }
        });

        botSelectionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onBotSelected();
            }
        });
    }

}
