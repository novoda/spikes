package com.novoda.spike.listrefreshspike2;

import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class MainActivity extends Activity implements Handler.Callback {

    public static final int WHAT = 99;
    private Handler handler;
    private SimpleListFragment slf1;
    private boolean noMoreBgUpdates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new Handler(this);
        handler.sendEmptyMessageDelayed(MainActivity.WHAT, 400L);
        setContentView(R.layout.main);
        FragmentManager fragmentManager = getFragmentManager();
        slf1 = (SimpleListFragment) fragmentManager.findFragmentById(R.id.simple_list_fragment1);
    }

    @Override
    protected void onResume() {
        super.onResume();
        noMoreBgUpdates = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeMessages(WHAT);
        noMoreBgUpdates = true;
    }

    @Override
    public boolean handleMessage(Message msg) {
        if (msg.what == WHAT && !noMoreBgUpdates) {
            new DataUpdateTask(getApplicationContext(), slf1, handler).execute();
            return true;
        }
        return false;
    }
}
