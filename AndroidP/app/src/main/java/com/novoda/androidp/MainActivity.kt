package com.novoda.androidp

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.novoda.androidp.pt651_dispplay_cutout.CutoutActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<View>(R.id.btn_cutout_activity).setOnClickListener {
            startActivity(Intent(applicationContext, CutoutActivity::class.java))
        }
    }

}
