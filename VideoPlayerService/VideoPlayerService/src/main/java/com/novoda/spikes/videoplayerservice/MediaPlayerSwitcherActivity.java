package com.novoda.spikes.videoplayerservice;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

@SuppressWarnings("ConstantConditions")
public class MediaPlayerSwitcherActivity extends Activity {

    private static final int PICK_VIDEO_REQUEST = 1001;
    private static final String TAG = "MediaPlayerSwitcherActivity";
    private SurfaceHolder mFirstSurface;
    private SurfaceHolder mSecondSurface;
    private SurfaceHolder mActiveSurface;
    private Uri mVideoUri;
    private VideoPlayerService service;
    private ServiceConnection conn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mediaplayer_switcher);
        SurfaceView first = (SurfaceView) findViewById(R.id.firstSurface);
        first.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                Log.d(TAG, "First surface created!");
                mFirstSurface = surfaceHolder;
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                Log.d(TAG, "First surface destroyed!");
            }
        });
        SurfaceView second = (SurfaceView) findViewById(R.id.secondSurface);
        second.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                Log.d(TAG, "Second surface created!");
                mSecondSurface = surfaceHolder;
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                Log.d(TAG, "Second surface destroyed!");
            }
        });
        startService();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mFirstSurface = null;
        mVideoUri = null;
        if (service != null){
            service.setDisplay(null);
        }
    }

    @Override
    protected void onDestroy() {
        unbindService(conn);
        super.onDestroy();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_VIDEO_REQUEST && resultCode == RESULT_OK) {
            Log.d(TAG, "Got video " + data.getData());
            mVideoUri = data.getData();
            if (service != null){
                service.setVideoData(mVideoUri.toString());
            }
        }
    }

    public void doStartStop(View view) {
        if (service == null || !service.isPlaying()) {
            Intent pickVideo = new Intent(Intent.ACTION_PICK);
            pickVideo.setType("video/*");
            startActivityForResult(pickVideo, PICK_VIDEO_REQUEST);
        } else {
            service.stopPlaying();
            service = null;
        }
    }

    public void doSwitchSurface(View view) {
        if (service != null && service.isPlaying()) {
            mActiveSurface = mFirstSurface == mActiveSurface ? mSecondSurface : mFirstSurface;
            service.setDisplay(mActiveSurface);
        }
    }


    public void startService(){
        startService(new Intent(this, VideoPlayerService.class));

        conn = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {

                service = ((VideoPlayerService.Binder) iBinder).getService();

                if (mVideoUri != null) {
                    service.setVideoData(mVideoUri.toString());
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {

            }
        };
        bindService(new Intent(this, VideoPlayerService.class), conn, BIND_AUTO_CREATE);
    }
}
