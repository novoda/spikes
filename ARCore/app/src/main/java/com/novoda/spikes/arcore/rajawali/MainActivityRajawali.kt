package com.novoda.spikes.arcore.rajawali

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.novoda.spikes.arcore.R
import kotlinx.android.synthetic.main.activity_main_rajawali.*
import org.rajawali3d.view.ISurface

class MainActivityRajawali : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_rajawali)

        surfaceView.setFrameRate(60.0)
        surfaceView.renderMode = ISurface.RENDERMODE_WHEN_DIRTY

        surfaceView.setSurfaceRenderer(BasicRenderer(this))

    }

}
