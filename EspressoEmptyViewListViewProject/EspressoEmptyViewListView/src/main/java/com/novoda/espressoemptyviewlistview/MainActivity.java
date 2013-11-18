package com.novoda.espressoemptyviewlistview;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;

import static android.os.SystemClock.sleep;

public class MainActivity extends Activity {

    private static final int LONG_TASK_DURATION_MS = 3000;
    private static final int SET_NEW_ADAPTER = 1;
    private static final int NOTIFY_DATA_SET_CHANGED_ADAPTER = 2;

    private GridView gridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        setupGridView();

        loadData();
    }

    private void setupGridView() {
        gridView = (GridView) findViewById(R.id.grid);

        gridView.setAdapter(new EmptyAdapter());

        View emptyView = findViewById(R.id.empty_view);
        gridView.setEmptyView(emptyView);
    }

    private void loadData() {
        new AsyncTask<Void, Integer, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                sleep(LONG_TASK_DURATION_MS);
                publishProgress(SET_NEW_ADAPTER);
                sleep(LONG_TASK_DURATION_MS);
                publishProgress(NOTIFY_DATA_SET_CHANGED_ADAPTER);
                sleep(LONG_TASK_DURATION_MS);
                return null;
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                final Integer action = values[0];
                switch (action) {
                    case SET_NEW_ADAPTER:
                        gridView.setAdapter(new LoremAdapter());
                        break;
                    case NOTIFY_DATA_SET_CHANGED_ADAPTER:
                        ((BaseAdapter) gridView.getAdapter()).notifyDataSetChanged();
                        break;
                }
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                LoremAdapter loremAdapter = new LoremAdapter();
                gridView.setAdapter(loremAdapter);
                loremAdapter.setLoading(false);
            }
        }.execute();
    }

    private static final class EmptyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return 0;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return null;
        }
    }

}
