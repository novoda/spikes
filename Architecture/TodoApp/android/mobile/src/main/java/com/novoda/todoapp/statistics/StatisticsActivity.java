package com.novoda.todoapp.statistics;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.novoda.todoapp.R;
import com.novoda.todoapp.TodoApplication;
import com.novoda.todoapp.statistics.displayer.StatisticsDisplayer;
import com.novoda.todoapp.statistics.presenter.StatisticsPresenter;

public class StatisticsActivity extends AppCompatActivity {

    private StatisticsPresenter presenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.statistics_activity);
        presenter = new StatisticsPresenter(
                TodoApplication.STATISTICS_SERVICE,
                ((StatisticsDisplayer) findViewById(R.id.content))
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
