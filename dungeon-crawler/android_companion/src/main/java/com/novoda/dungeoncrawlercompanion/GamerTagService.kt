package com.novoda.dungeoncrawlercompanion

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

private const val GAMER_REFERENCE: String = "currentGamerTag"

class GamerTagService(private val database: FirebaseDatabase = FirebaseDatabase.getInstance()) {

    private val listener: ValueEventListener = createListener()
    private var callback: (String) -> Unit = {}

    private fun createListener(): ValueEventListener {
        return object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (!dataSnapshot.exists() || dataSnapshot.value == null) {
                    return
                }
                callback(dataSnapshot.value as String)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // not used
            }
        }
    }

    fun onChange(callback: (String) -> Unit) {
        this.callback = callback
        val reference = database.getReference(GAMER_REFERENCE)
        reference.addValueEventListener(listener)
    }

    fun disconnect() {
        this.callback = {}
        val reference = database.getReference(GAMER_REFERENCE)
        reference.removeEventListener(listener)
    }

    fun set(gamerTag: String, onSuccess: () -> Unit, onError: () -> Unit) {
        val reference = database.getReference(GAMER_REFERENCE)
        reference.setValue(gamerTag) { databaseError, _ ->
            when (databaseError) {
                null -> onSuccess()
                else -> onError()
            }
        }
    }

}
