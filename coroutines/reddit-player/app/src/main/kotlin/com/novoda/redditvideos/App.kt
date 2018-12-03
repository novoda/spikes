package com.novoda.redditvideos

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.novoda.reddit.data.RedditApi
import com.novoda.reddit.data.createRetrofit
import com.novoda.redditvideos.model.AppDatabase

class App : Application() {

    val database by lazy { Room.databaseBuilder(this, AppDatabase::class.java, "database").build() }
    val apiClient by lazy { RedditApi(createRetrofit("https://reddit.com")) }

}

inline val Context.apiClient: RedditApi get() = (applicationContext as App).apiClient
inline val Context.database: AppDatabase get() = (applicationContext as App).database
