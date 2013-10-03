package com.novoda.spike.listrefreshspike2;

import android.content.ContentResolver;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;

/**
 * Created by Paul on 03/10/2013.
 */
public class DataUpdateTask extends AsyncTask<Void, Void, Void> {

    private Context context;
    private SimpleListFragment simpleListFragment;
    private Handler handler;

    public DataUpdateTask(Context context, SimpleListFragment simpleListFragment, Handler handler) {
        this.context = context;
        this.simpleListFragment = simpleListFragment;
        this.handler = handler;
    }

    @Override
    protected Void doInBackground(Void... params) {
        ContentResolver contentResolver = context.getContentResolver();
        contentResolver.insert(SimpleListFragment.STUFF_URI, null);
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
            simpleListFragment.refresh();
            handler.sendEmptyMessageDelayed(MainActivity.WHAT, 100L);
    }
}
