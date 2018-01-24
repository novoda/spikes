package com.novoda.spikes.keyboarddemo

import android.annotation.SuppressLint
import android.inputmethodservice.InputMethodService
import android.inputmethodservice.Keyboard
import android.inputmethodservice.KeyboardView
import android.view.KeyEvent
import android.view.View
import android.view.Window
import android.view.inputmethod.CompletionInfo
import android.widget.Toast

class NovodaInputMethod : InputMethodService(), KeyboardView.OnKeyboardActionListener {
    override fun swipeRight() {

    }

    override fun onPress(p0: Int) {

    }

    override fun onRelease(p0: Int) {

    }

    override fun swipeLeft() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun swipeUp() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun swipeDown() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    override fun onKey(primaryCode: Int, keyCodes: IntArray?) {
        when (primaryCode) {
            Keyboard.KEYCODE_DELETE -> currentInputConnection?.deleteSurroundingText(1, 0)
            else -> currentInputConnection?.commitText(primaryCode.toChar().toString(), 1)
        }
    }


    override fun onText(p0: CharSequence?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        Toast.makeText(applicationContext, event.toString(), Toast.LENGTH_SHORT).show()
        return super.onKeyDown(keyCode, event)
    }

    override fun onBindInput() {
        super.onBindInput()
    }

    override fun onConfigureWindow(win: Window?, isFullscreen: Boolean, isCandidatesOnly: Boolean) {
        super.onConfigureWindow(win, isFullscreen, isCandidatesOnly)
    }

    override fun onDisplayCompletions(completions: Array<out CompletionInfo>?) {
        super.onDisplayCompletions(completions)
    }

    @SuppressLint("InflateParams")
    override fun onCreateInputView(): View {
        val inputView: KeyboardView = layoutInflater.inflate(R.layout.keyboard_layout, null) as KeyboardView
        val keyboard = Keyboard(this, R.xml.qwerty)
        inputView.setOnKeyboardActionListener(this)
        inputView.keyboard = keyboard

        return inputView
    }

}
