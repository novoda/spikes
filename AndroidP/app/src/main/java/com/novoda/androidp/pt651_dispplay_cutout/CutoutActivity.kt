package com.novoda.androidp.pt651_dispplay_cutout

import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.novoda.androidp.R

class CutoutActivity : AppCompatActivity() {

    private lateinit var linearLayout: View
    private val CUTOUT_MODE = "cutout_mode"
    private val CUTOUT_MODE_DEFAULT = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_DEFAULT

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cutout)
        
        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        val cutoutMode = pref.getInt(CUTOUT_MODE, CUTOUT_MODE_DEFAULT)
        window.attributes.layoutInDisplayCutoutMode = cutoutMode

        findViewById<View>(R.id.btn_cutout_always).setOnClickListener {
            pref.edit().putInt(CUTOUT_MODE, WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS).commit()
            restart()
        }

        findViewById<View>(R.id.btn_cutout_never).setOnClickListener {
            pref.edit().putInt(CUTOUT_MODE, WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_NEVER).commit()
            restart()
        }

        findViewById<View>(R.id.btn_cutout_default).setOnClickListener {
            pref.edit().putInt(CUTOUT_MODE, WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_DEFAULT).commit()
            restart()
        }
        linearLayout = findViewById(R.id.main_layout)
        linearLayout.setOnApplyWindowInsetsListener { view, windowInsets ->
            windowInsets.displayCutout?.let {
                Toast.makeText(this, windowInsets.displayCutout.bounds.toString(), Toast.LENGTH_SHORT).show()
            }

            windowInsets
        }

    }

    private fun restart() {
        startActivity(getIntent());
        finish();
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            linearLayout.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        }
    }
}
