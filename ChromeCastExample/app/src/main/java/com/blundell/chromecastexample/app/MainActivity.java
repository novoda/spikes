package com.blundell.chromecastexample.app;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.blundell.chromecastexample.app.cast.LocalPlayerActivity;
import com.google.android.gms.cast.MediaInfo;
import com.google.android.gms.cast.MediaMetadata;
import com.google.android.gms.common.images.WebImage;
import com.google.sample.castcompanionlibrary.utils.Utils;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.button_go_cast).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                String videoUrl = "http://artestras.vo.llnwd.net/o35/geo/arte7/ALL/tvguide/044049-000-A_HQ_1_VOA_01289521_MP4-2200_AMM-HLS/044049-000-A_HQ_1_VOA_01289521_MP4-2200_AMM-HLS.m3u8";
                String videoUrl = "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4#sthash.rJ5rPZy8.dpuf";
                String title = "Jahre Philharmonie";
                String description = "Awesome film you must watch!";
                String author = "Blundell";

                String thumbUrl = "http://www.ustream.tv/blog/wp-content/uploads/2013/10/simply-go-live.png";

                MediaMetadata metadata = new MediaMetadata(MediaMetadata.MEDIA_TYPE_MOVIE);
                metadata.addImage(new WebImage(Uri.parse(thumbUrl)));
                metadata.addImage(new WebImage(Uri.parse(thumbUrl))); // Needs two images or fail lolz
                metadata.putString(MediaMetadata.KEY_TITLE, title);
                metadata.putString(MediaMetadata.KEY_SUBTITLE, description);
                metadata.putString(MediaMetadata.KEY_STUDIO, author);
                MediaInfo mediaInfo = new MediaInfo.Builder(videoUrl)
                        .setMetadata(metadata)
                        .setStreamType(MediaInfo.STREAM_TYPE_BUFFERED)
                        .setContentType("video/mp4")
                        .build();

                Intent intent = new Intent(MainActivity.this, LocalPlayerActivity.class);
                intent.putExtra("media", Utils.fromMediaInfo(mediaInfo));
                intent.putExtra("shouldStart", true);
                startActivity(intent);
            }
        });
    }

}
