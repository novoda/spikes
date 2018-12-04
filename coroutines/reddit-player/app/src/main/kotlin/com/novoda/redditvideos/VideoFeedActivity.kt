package com.novoda.redditvideos

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.get
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.novoda.redditvideos.VideoFeedState.Loading
import com.novoda.redditvideos.VideoFeedState.LoadingWithCache
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

        refresh.setOnRefreshListener {
            viewModel.reload()
        }

        viewModel.state.observe(this, ::render)
    }

    private fun render(state: VideoFeedState) {
        feedAdapter.state = state
        if (state is VideoFeedState.Failure) Log.e(TAG, state.message, state.exception)
        refresh.isRefreshing = state is VideoFeedState.LoadingWithCache
    }

}

private val TAG = VideoFeedActivity::class.java.simpleName
