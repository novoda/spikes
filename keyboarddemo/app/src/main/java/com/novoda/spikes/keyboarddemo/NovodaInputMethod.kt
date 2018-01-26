package com.novoda.spikes.keyboarddemo

import android.annotation.SuppressLint
import android.inputmethodservice.InputMethodService
import android.inputmethodservice.Keyboard
import android.inputmethodservice.KeyboardView
import android.view.View


class NovodaInputMethod : InputMethodService() {
    val SPACEBAR = 32
    var composing = StringBuilder()
    val pMap = mapOf(
            "hello" to "ahoy",
            "hi" to "yo-ho-ho",
            "pardon me" to "avast",
            "excuse me" to "arrr",
            "my" to "me",
            "friend" to "me bucko",
            "sir" to "matey",
            "madam" to "proud beauty",
            "stranger" to "scurvy dog",
            "officer" to "foul blaggart",
            "where" to "whar",
            "is" to "be",
            "the" to "th'",
            "you" to "ye",
            "tell" to "be tellin'",
            "know" to "be knowin'",
            "how far" to "how many leagues",
            "old" to "barnacle-covered",
            "attractive" to "comely",
            "happy" to "grog-filled",
            "nearby" to "broadside",
            "restroom" to "head",
            "restaurant" to "galley",
            "hotel" to "fleabag inn",
            "pub" to "Scumm Bar",
            "bank" to "buried treasure"
    )

    @SuppressLint("InflateParams")
    override fun onCreateInputView(): View {
        val inputView: KeyboardView = layoutInflater.inflate(R.layout.keyboard_layout, null) as KeyboardView
        inputView.keyboard = Keyboard(this, R.xml.qwerty)
        inputView.setOnKeyboardActionListener(object : SimpleKeyboardActionListener() {
            override fun onKey(primaryKeycode: Int, keyCodes: IntArray?) {
                handleKeyCode(primaryKeycode)
            }
        })

        return inputView
    }

    private fun handleKeyCode(primaryCode: Int) {
        when (primaryCode) {
            Keyboard.KEYCODE_DELETE -> {
                currentInputConnection?.deleteSurroundingText(1, 0)
                if (composing.length > 0) {
                    composing = StringBuilder(composing.removeRange(composing.length - 1, composing.length))
                }
            }
            SPACEBAR -> {
                replacePirate(composing.toString())
                currentInputConnection?.commitText(primaryCode.toChar().toString(), 1)
                composing = StringBuilder()
            }
            else -> {
                currentInputConnection?.commitText(primaryCode.toChar().toString(), 1)
                composing.append(primaryCode.toChar().toString())
            }
        }
    }

    private fun replacePirate(input: String) {
        pMap[input.trim()]?.apply {
            currentInputConnection?.deleteSurroundingText(input.length, 0)
            currentInputConnection?.commitText(this, 1)
        }
    }

}
