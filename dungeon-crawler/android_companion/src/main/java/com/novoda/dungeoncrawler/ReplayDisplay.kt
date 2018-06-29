package com.novoda.dungeoncrawler

import android.content.Context
import android.graphics.Color
import android.os.Handler
import android.view.ViewGroup
import android.widget.LinearLayout
import java.util.ArrayList

private const val OPAQUE = 255

class ReplayDisplay(context: Context, containerView: ViewGroup, numberOfLeds: Int) : Display {
    private val state: MutableList<Int>
    private val linearLayout: LinearLayout
    private val handler: Handler

    init {
        this.state = ArrayList(numberOfLeds)
        linearLayout = LinearLayout(context)
        linearLayout.layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        linearLayout.orientation = LinearLayout.HORIZONTAL
        for (i in 0 until numberOfLeds) {
            val ledView = AndroidLedView(context)
            ledView.layoutParams = LinearLayout.LayoutParams(24, 24)
            ledView.id = 999 + i
            linearLayout.addView(ledView, i)
            state.add(i, Color.BLACK)
        }
        containerView.addView(linearLayout)
        handler = Handler(context.mainLooper)
    }

    override fun clear() {
        val numberOfLeds = state.size
        for (i in 0 until numberOfLeds) {
            state[i] = Color.BLACK
        }
    }

    override fun show() {
        val current = ArrayList<Int>(state.size)
        for (i in state.indices) {
            current.add(i, state[i])
        }
        handler.removeCallbacksAndMessages(null)
        handler.post { updateGameMap(current) }
    }

    private fun updateGameMap(current: List<Int>) {
        for (i in current.indices) {
            val integer = current[i]
            val ledView = linearLayout.getChildAt(i) as AndroidLedView
            ledView.setBackgroundColor(integer)
        }
    }

    override fun set(position: Int, rgb: Display.CRGB) {
        state[position] = Color.argb(OPAQUE, rgb.red, rgb.green, rgb.blue)
    }

    override fun set(position: Int, hsv: Display.CHSV) {
        state[position] = Color.HSVToColor(OPAQUE, floatArrayOf(hsv.hue.toFloat(), hsv.sat.toFloat(), hsv.`val`.toFloat()))
    }

    override fun modifyHSV(position: Int, hue: Int, saturation: Int, value: Int) {
        // TODO is this correct
        set(position, Display.CHSV(hue, saturation, value))
    }

    override fun modifyScale(position: Int, scale: Int) {
        transform(position, { colourComponent -> colourComponent.toInt() }) // TODO: Apply scale
    }

    private fun transform(position: Int, transformation: (Float) -> Int) {
        val color = Color.valueOf(state[position])
        val scaled = Color.argb(OPAQUE, transformation(color.red()), transformation(color.green()), transformation(color.blue()))
        state[position] = scaled
    }

    override fun modifyMod(position: Int, mod: Int) {
        transform(position, { colourComponent -> colourComponent.toInt() % mod }) // TODO is this correct
    }

}
