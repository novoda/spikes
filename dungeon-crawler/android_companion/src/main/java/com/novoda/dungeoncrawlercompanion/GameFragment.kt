package com.novoda.dungeoncrawlercompanion

import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.novoda.dungeoncrawler.ArduinoLoop
import com.novoda.dungeoncrawler.DungeonCrawlerGame
import com.novoda.dungeoncrawler.InitHack
import com.novoda.dungeoncrawler.JoystickActuator
import com.novoda.dungeoncrawler.ReplayDisplay
import com.novoda.dungeoncrawler.Screensaver
import kotlinx.android.synthetic.main.fragment_game.*

private const val NUM_OF_SQUARES = 300

class GameFragment : Fragment() {

    private val gamerTagService: GamerTagService by lazy { GamerTagService() }
    private var game: DungeonCrawlerGame? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_game, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val display = ReplayDisplay(view.context, replay_container, NUM_OF_SQUARES)
        val screensaver = Screensaver(display, NUM_OF_SQUARES)
        val looper = ArduinoLoop()
        game = InitHack.newInstance(NUM_OF_SQUARES, display, { /* no-op */ }, { JoystickActuator.JoyState() }, NoOpSoundEffectsPlayer(), screensaver, looper)

        play_pause.setOnClickListener { GamePauser().toggle() }
        gamer_tag.onTextChanged { update_gamer_tag.enable() }
        update_gamer_tag.setOnClickListener { updateGamerTag() }
    }

    private fun Button.enable() {
        isEnabled = true
    }

    private fun updateGamerTag() {
        gamerTagService.set(
                gamer_tag.text.toString(),
                onSuccess = { update_gamer_tag.disable() },
                onError = { Toast.makeText(activity, R.string.error_updating_gamer_tag, Toast.LENGTH_LONG).show() }
        )
    }

    private fun Button.disable() {
        isEnabled = false
    }

    private fun EditText.onTextChanged(runnable: () -> Unit) {
        addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // not needed
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // not needed
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                runnable()
            }

        })
    }

    override fun onResume() {
        super.onResume()
        gamerTagService.onChange { current_gamer_tag.text = getString(R.string.current_gamer_tag, it) }
        game?.start()
    }

    override fun onPause() {
        super.onPause()
        gamerTagService.disconnect()
        game?.stop()
    }
}
