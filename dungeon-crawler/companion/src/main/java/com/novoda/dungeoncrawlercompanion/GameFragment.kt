package com.novoda.dungeoncrawlercompanion

import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_game.*

class GameFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_game, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        play_pause.setOnClickListener { GamePauser().toggle() }

        val gamerTagService = GamerTagService()

        gamerTagService.onChange { current_gamer_tag.text = getString(R.string.current_gamer_tag, it) }

        gamer_tag.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // not needed
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // not needed
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                update_gamer_tag.isEnabled = true
            }

        })

        update_gamer_tag.setOnClickListener {
            gamerTagService.set(
                    gamer_tag.text.toString(),
                    onSuccess = {
                        update_gamer_tag.isEnabled = false
                    },
                    onError = {
                        Toast.makeText(activity, R.string.error_updating_gamer_tag, Toast.LENGTH_LONG).show()
                    }
            )
        }

    }
}
