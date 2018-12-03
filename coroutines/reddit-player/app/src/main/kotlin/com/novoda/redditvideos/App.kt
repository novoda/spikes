package com.novoda.redditvideos

import android.app.Application
import com.novoda.reddit.data.createRetrofit

class App : Application() {

    val retrofit by lazy { createRetrofit("https://reddit.com") }

}

inline val Application.retrofit get() = (this as App).retrofit
