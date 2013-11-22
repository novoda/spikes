package com.novoda.spikes.videoplayerservice;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.view.SurfaceHolder;

import java.io.IOException;

public class VideoPlayerService extends Service implements MediaPlayer.OnPreparedListener {

    private MediaPlayer mediaPlayer;
    private String currentUrl;

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnPreparedListener(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new Binder();
    }

    public void setVideoData(String videoUrl) {
        if (currentUrl != null && currentUrl.equals(videoUrl) && mediaPlayer.isPlaying()){
            // continue playing
            return;
        }

        currentUrl = videoUrl;
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(videoUrl);
            mediaPlayer.prepareAsync();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
       mediaPlayer.start();
       startForeground(1, new Notification());
    }

    public void setDisplay(SurfaceHolder surfaceHolder) {
        mediaPlayer.setDisplay(surfaceHolder);
    }

    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    public void stopPlaying() {
        mediaPlayer.reset();
    }

    @Override
    public boolean stopService(Intent name) {
        mediaPlayer.release();
        return super.stopService(name);
    }

    public void togglePauseResume() {
        if (mediaPlayer.isPlaying()){
            mediaPlayer.pause();
        } else {
            mediaPlayer.start();
        }
    }

    public class Binder extends android.os.Binder {
        public VideoPlayerService getService() {
            return VideoPlayerService.this;
        }
    }
}
