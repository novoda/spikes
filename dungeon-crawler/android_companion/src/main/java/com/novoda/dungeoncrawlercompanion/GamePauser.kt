package com.novoda.dungeoncrawlercompanion

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class GamePauser(private val database: FirebaseDatabase = FirebaseDatabase.getInstance()) {

    fun toggle() {
        val pausedReference = database.getReference("game/paused")
        pausedReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (!dataSnapshot.exists()) {
                    pause()
                    return
                }
                val paused = dataSnapshot.value as Boolean
                if (paused) {
                    resume()
                } else {
                    pause()
                }
            }

            private fun resume() {
                pausedReference.setValue(false)
            }

            private fun pause() {
                pausedReference.setValue(true)
            }

            override fun onCancelled(databaseError: DatabaseError) {
//                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

        })
    }

}
