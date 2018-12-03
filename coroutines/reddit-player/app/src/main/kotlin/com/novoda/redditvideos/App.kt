package com.novoda.redditvideos

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.novoda.reddit.data.createRetrofit
import com.novoda.redditvideos.model.AppDatabase
import retrofit2.Retrofit

class App : Application() {

    val retrofit by lazy { createRetrofit("https://reddit.com") }
    val database by lazy { Room.databaseBuilder(this, AppDatabase::class.java, "database").build() }

}

inline val Context.retrofit: Retrofit get() = (this as App).retrofit
inline val Context.database: AppDatabase get() = (applicationContext as App).database
