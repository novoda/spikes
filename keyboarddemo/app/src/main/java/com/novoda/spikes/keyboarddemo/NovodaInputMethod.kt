package com.novoda.spikes.keyboarddemo

import android.annotation.SuppressLint
import android.content.Context
import android.inputmethodservice.InputMethodService
import android.inputmethodservice.Keyboard
import android.inputmethodservice.KeyboardView
import android.os.Build
import android.support.annotation.RequiresApi
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.Window
import android.view.inputmethod.CompletionInfo
import android.view.inputmethod.EditorInfo
import android.view.textservice.SentenceSuggestionsInfo
import android.view.textservice.SpellCheckerSession
import android.view.textservice.SuggestionsInfo
import android.view.textservice.TextServicesManager
import android.widget.Toast
import java.util.*


class NovodaInputMethod : InputMethodService(), KeyboardView.OnKeyboardActionListener, SpellCheckerSession.SpellCheckerSessionListener {
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun onGetSentenceSuggestions(results: Array<out SentenceSuggestionsInfo>?) {
        val sb = StringBuffer("")
        if (results != null) {
            for (result in results) {
                val n = result.getSuggestionsCount()
                for (i in 0 until n) {
                    val m = result.getSuggestionsInfoAt(i).getSuggestionsCount()

                    for (k in 0 until m) {
                        sb.append(result.getSuggestionsInfoAt(i).getSuggestionAt(k))
                                .append("\n")
                    }
                    sb.append("\n")
                }
            }
        }
        Log.d("NovodaInputMethod", sb.toString())
        Toast.makeText(applicationContext, sb.toString(), Toast.LENGTH_SHORT).show()
    }

    override fun onGetSuggestions(p0: Array<out SuggestionsInfo>?) {
        val i = 0
    }

    var session: SpellCheckerSession? = null

    var composing = StringBuilder()
    val pirateMap = arrayOf(
            TranslationRule("hello", "ahoy"),
            TranslationRule("hi", "yo-ho-ho"),
            TranslationRule("pardon me", "avast"),
            TranslationRule("excuse me", "arrr"),
            TranslationRule("my", "me"),
            TranslationRule("friend", "me bucko"),
            TranslationRule("sir", "matey"),
            TranslationRule("madam", "proud beauty"),
            TranslationRule("stranger", "scurvy dog"),
            TranslationRule("officer", "foul blaggart"),
            TranslationRule("where", "whar"),
            TranslationRule("is", "be"),
            TranslationRule("the", "th'"),
            TranslationRule("you", "ye"),
            TranslationRule("tell", "be tellin'"),
            TranslationRule("know", "be knowin'"),
            TranslationRule("how far", "how many leagues"),
            TranslationRule("old", "barnacle-covered"),
            TranslationRule("attractive", "comely"),
            TranslationRule("happy", "grog-filled"),
            TranslationRule("nearby", "broadside"),
            TranslationRule("restroom", "head"),
            TranslationRule("restaurant", "galley"),
            TranslationRule("hotel", "fleabag inn"),
            TranslationRule("pub", "Skull & Scuppers"),
            TranslationRule("bank", "buried treasure"))

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


    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun onKey(primaryCode: Int, keyCodes: IntArray?) {

        when (primaryCode) {
            Keyboard.KEYCODE_DELETE -> {
                currentInputConnection?.deleteSurroundingText(1, 0)
                if (composing.length > 0) {
                    composing = StringBuilder(composing.removeRange(composing.length - 1, composing.length))
                }
            }
            // Spacebar
            32 -> {
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
        val rule : TranslationRule?   = pirateMap.find {
            it.english.equals(input.trim().toLowerCase(Locale.UK))
        }

        if (rule != null) {
            currentInputConnection?.deleteSurroundingText(rule.english.length, 0)
            currentInputConnection?.commitText(rule.pirate, 1)
        }
//        pirateMap
//                .filter { it.english.equals(input.toLowerCase(Locale.UK)) }
//                .map { currentInputConnection?.commitText(it.pirate, 1) }
    }


    override fun onText(p0: CharSequence?) {
        val i = 0
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        onKey(keyCode, null)
        return false
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

    override fun onFinishInput() {
        super.onFinishInput()
    }


    override fun onStartInput(attribute: EditorInfo?, restarting: Boolean) {
        super.onStartInput(attribute, restarting)

        val tsm = getSystemService(Context.TEXT_SERVICES_MANAGER_SERVICE) as TextServicesManager

        session = tsm.newSpellCheckerSession(null, Locale.ENGLISH, this, true)


    }


}
