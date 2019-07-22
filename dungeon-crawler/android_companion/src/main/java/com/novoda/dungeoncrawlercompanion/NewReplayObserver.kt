package com.novoda.dungeoncrawlercompanion

import android.util.Log
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.novoda.dungeoncrawler.FirebaseGameStateLogger
import com.novoda.dungeoncrawler.Redux
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types

class NewReplayObserver(private val firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()) {

    private val moshi = Moshi.Builder()
            .add(FirebaseGameStateLogger.GameStateAdapter())
            .build()
    private val adapter = moshi.adapter(Redux.GameState::class.java)
    private val type = Types.newParameterizedType(List::class.java, String::class.java)
    private val framesAdapter = moshi.adapter<List<String>>(type)
    private var alreadyObserving: Boolean = false

    fun observeForReplays(callback: (List<Redux.GameState>, String) -> Unit) {
        if (alreadyObserving) {
            return
        }

        alreadyObserving = true
        val reference = firebaseDatabase.getReference("games")
        reference.onChildAdded {
            if (!it.containsKey("frames") || !it.containsKey("gamerTag")) {
                Log.e("foo", "No Frames found!")
                return@onChildAdded
            }
            callback(it.getGameStates(), it.getGamerTag())
        }
    }

    private fun HashMap<String, *>.getGamerTag() = this@getGamerTag["gamerTag"] as String

    private fun HashMap<String, *>.getGameStates(): List<Redux.GameState> {
        val rawFrames = this@getGameStates["frames"] as String
        val frames = framesAdapter.fromJson(rawFrames) as List<String>
        return frames.map(adapter::fromJson) as List<Redux.GameState>
    }

    private fun DatabaseReference.onChildAdded(childAddedCallback: (HashMap<String, *>) -> Unit) {
        addChildEventListener(object : ChildEventListener {
            override fun onCancelled(p0: DatabaseError) {
                // no-op
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
                // no-op
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                // no-op
            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                Log.e("foo", "new child " + p0.key)
                childAddedCallback(p0.value as HashMap<String, *>)
            }

            override fun onChildRemoved(p0: DataSnapshot) {
                // no-op
            }

        })
    }

}
