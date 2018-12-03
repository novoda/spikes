package com.novoda.redditvideos

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.get
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.novoda.redditvideos.support.lifecycle.observe
import kotlinx.android.synthetic.main.activity_main.*

class VideoFeedActivity : AppCompatActivity() {

    private val feedAdapter = VideoFeedAdapter()

    private val viewModel by lazy { ViewModelProviders.of(this).get<VideoFeedViewModel>() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        content.adapter = feedAdapter
        content.layoutManager = LinearLayoutManager(this)
        content.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        viewModel.state.observe(this) { newState ->
            feedAdapter.state = newState
            if (newState is VideoFeedState.Failure) {
                Log.e("TAG", newState.message, newState.exception)
            }
        }
    }

}

