package com.novoda.dungeoncrawlercompanion

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

private const val GAMER_REFERENCE: String = "currentGamerTag"

class GamerTagService(private val database: FirebaseDatabase = FirebaseDatabase.getInstance()) {

    fun onChange(callback: (String) -> Unit) {
        val reference = database.getReference(GAMER_REFERENCE)
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (!dataSnapshot.exists()) {
                    return
                }
                callback(dataSnapshot.value as String)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // not used
            }
        })
    }

    fun set(gamerTag: String, onSuccess: () -> Unit, onError: () -> Unit) {
        val reference = database.getReference(GAMER_REFERENCE)
        reference.setValue(gamerTag) { databaseError, _ ->
            if (databaseError == null) {
                onSuccess()
            } else {
                onError()
            }
        }
    }

}
