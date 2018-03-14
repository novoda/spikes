package com.novoda.androidp

import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v7.app.AppCompatActivity
import android.view.DisplayCutout
import android.view.View
import android.view.WindowInsets
import com.novoda.androidp.R.id.main_layout

class MainActivity : AppCompatActivity() {

    private lateinit var mainLayout: ConstraintLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mainLayout = findViewById<ConstraintLayout>(main_layout) as ConstraintLayout

        val listener: View.OnApplyWindowInsetsListener = InsetListener()

        mainLayout.setOnApplyWindowInsetsListener(listener)

    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            mainLayout.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        }
    }

    class InsetListener : View.OnApplyWindowInsetsListener {

        override fun onApplyWindowInsets(p0: View?, p1: WindowInsets): WindowInsets {
            val displayCutout: DisplayCutout = p1.displayCutout
            val int = 0

            return p1
        }

    }
}
