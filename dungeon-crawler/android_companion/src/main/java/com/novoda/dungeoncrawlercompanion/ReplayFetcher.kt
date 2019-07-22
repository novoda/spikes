package com.novoda.dungeoncrawlercompanion

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.novoda.dungeoncrawler.FirebaseGameStateLogger
import com.novoda.dungeoncrawler.Redux
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types

class ReplayFetcher(private val firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()) {

    private val moshi = Moshi.Builder()
            .add(FirebaseGameStateLogger.GameStateAdapter())
            .build()
    private val adapter = moshi.adapter(Redux.GameState::class.java)
    private val type = Types.newParameterizedType(List::class.java, String::class.java)
    private val framesAdapter = moshi.adapter<List<String>>(type)

    fun fetchReplay(replayId: String, callback: (List<Redux.GameState>, String) -> Unit) {
        firebaseDatabase.getReference("games/$replayId").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    val map = p0.value as HashMap<String, *>
                    val rawFrames = map["frames"] as String
                    val frames = framesAdapter.fromJson(rawFrames) as List<String>
                    val states = frames.map(adapter::fromJson) as List<Redux.GameState>
                    callback(states, map["gamerTag"] as String) // need to store those pools somehow :(
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                // no-op
            }

        })
    }

}
