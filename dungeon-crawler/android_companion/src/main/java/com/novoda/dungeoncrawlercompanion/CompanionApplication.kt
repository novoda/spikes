package com.novoda.dungeoncrawlercompanion

import android.app.Application
import com.google.firebase.database.FirebaseDatabase

class CompanionApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
    }

}
