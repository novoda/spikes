package com.novoda.todoapp.statistics;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.novoda.notils.caster.Views;
import com.novoda.todoapp.R;
import com.novoda.todoapp.TodoApplication;
import com.novoda.todoapp.navigation.AndroidTopLevelMenuDisplayer;
import com.novoda.todoapp.navigation.AndroidNavigator;
import com.novoda.todoapp.statistics.presenter.StatisticsPresenter;
import com.novoda.todoapp.statistics.view.StatisticsView;

public class StatisticsActivity extends AppCompatActivity {

    private StatisticsPresenter presenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.statistics_activity);
        StatisticsView statisticsView = Views.findById(this, R.id.content);
        presenter = new StatisticsPresenter(
                TodoApplication.STATISTICS_SERVICE,
                statisticsView,
                new AndroidTopLevelMenuDisplayer(statisticsView, statisticsView.getNavDrawer()),
                new AndroidNavigator(this)
        );
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.startPresenting();
    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter.stopPresenting();
    }

}
