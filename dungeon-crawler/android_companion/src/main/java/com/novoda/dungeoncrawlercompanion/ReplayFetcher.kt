package com.novoda.dungeoncrawlercompanion

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ReplayFetcher(private val firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()) {

    fun fetchRandomReplay(callback: () -> Unit) {
        firebaseDatabase.getReference("games/1530035201458").addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    Log.e("foo", "" + p0.value)
                    callback()
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                // no-op
            }

        })
    }

}
