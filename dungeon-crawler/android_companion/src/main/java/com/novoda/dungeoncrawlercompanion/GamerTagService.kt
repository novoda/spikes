package com.novoda.dungeoncrawlercompanion

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

private const val GAMER_REFERENCE: String = "currentGamerTag"

class GamerTagService(private val database: FirebaseDatabase = FirebaseDatabase.getInstance()) {
    private var listener: ValueEventListener? = null

    fun onChange(callback: (String) -> Unit) {
        val reference = database.getReference(GAMER_REFERENCE)
        listener = callback.asValueEventListener()
        reference.addValueEventListener(listener)
    }

    private fun ((String) -> Unit).asValueEventListener(): ValueEventListener {
        return object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (!dataSnapshot.exists() || dataSnapshot.value == null) {
                    return
                }
                this@asValueEventListener(dataSnapshot.value as String)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // not used
            }
        }
    }

    fun disconnect() {
        if (listener != null) {
            val reference = database.getReference(GAMER_REFERENCE)
            reference.removeEventListener(listener)
        }
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
