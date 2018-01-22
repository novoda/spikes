package com.novoda.spikes.keyboarddemo

import android.inputmethodservice.InputMethodService
import android.view.KeyEvent
import android.view.Window

class NovodaInputMethod : InputMethodService() {

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return super.onKeyDown(keyCode, event)
    }

    override fun onBindInput() {
        super.onBindInput()
    }

    override fun onConfigureWindow(win: Window?, isFullscreen: Boolean, isCandidatesOnly: Boolean) {
        super.onConfigureWindow(win, isFullscreen, isCandidatesOnly)
    }

}