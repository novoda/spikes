package com.novoda.androidp

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.novoda.androidp.display_cutout.CutoutActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnCutoutActivity.setOnClickListener {
            startActivity(Intent(applicationContext, CutoutActivity::class.java))
        }
    }

}
