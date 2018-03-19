package com.novoda.androidp.pt651_display_cutout

import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.WindowManager
import android.widget.RadioGroup
import com.novoda.androidp.R
import kotlinx.android.synthetic.main.activity_cutout.*

class CutoutActivity : AppCompatActivity() {

    private val CUTOUT_MODE = "cutout_mode"
    private val CUTOUT_USE_INSET = "cutout_use_inset"
    private val CUTOUT_MODE_DEFAULT = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_DEFAULT

    val cutoutModeMap = mapOf(
            R.id.rd_cutout_default to WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_DEFAULT,
            R.id.rd_cutout_always to WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS,
            R.id.rd_cutout_never to WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_NEVER)

    val cutoutUseInsetMap = mapOf(
            R.id.rd_cutout_yes to true,
            R.id.rd_cutout_no to false
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cutout)
        setFullscreen()

        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        setLayoutCutoutMode(pref)
        setupTitleView(pref)
        setupRadioButtonForCutoutMode(pref)
        setupRadioButtonForInset(pref)
    }

    private fun setFullscreen() {
        getWindow().getDecorView().systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
    }

    private fun setLayoutCutoutMode(pref: SharedPreferences) {
        window.attributes.layoutInDisplayCutoutMode = cutoutModeMap[pref.getInt(CUTOUT_MODE, CUTOUT_MODE_DEFAULT)] ?: CUTOUT_MODE_DEFAULT
    }

    private fun setupTitleView(pref: SharedPreferences) {
        cutoutTitleView.setOnApplyWindowInsetsListener { _, windowInsets ->
            windowInsets.displayCutout?.let {
                if (isUseInsetSelected(pref)) {
                    val safeInsetTop = windowInsets.displayCutout.safeInsetTop
                    cutoutTitleView.y = cutoutTitleView.top + safeInsetTop.toFloat()
                }
            }

            windowInsets
        }
    }

    private fun isUseInsetSelected(pref: SharedPreferences) = cutoutUseInsetMap[pref.getInt(CUTOUT_USE_INSET, 0)] ?: false

    private fun setupRadioButtonForCutoutMode(pref: SharedPreferences) {
        val radioGroup = findViewById<RadioGroup>(R.id.rd_cutout_mode)
        radioGroup.check(pref.getInt(CUTOUT_MODE, R.id.rd_cutout_default))
        radioGroup.setOnCheckedChangeListener { radioGroup, checkedId ->
            pref.edit().putInt(CUTOUT_MODE, checkedId).commit()
            restart()
        }
    }

    private fun setupRadioButtonForInset(pref: SharedPreferences) {
        val radioGroup = findViewById<RadioGroup>(R.id.rd_cutout)
        radioGroup.check(pref.getInt(CUTOUT_USE_INSET, R.id.rd_cutout_no))
        radioGroup.setOnCheckedChangeListener { radioGroup, checkedId ->
            pref.edit().putInt(CUTOUT_USE_INSET, checkedId).commit()
            restart()
        }
    }

    private fun restart() {
        startActivity(getIntent());
        finish();
    }

}
