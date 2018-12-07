package com.novoda.redditvideos

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.get
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.novoda.redditvideos.model.VideoFeedState
import com.novoda.redditvideos.model.VideoFeedState.LoadState.*
import com.novoda.redditvideos.support.lifecycle.observe
import com.novoda.redditvideos.support.view.showOnly
import kotlinx.android.synthetic.main.include_content.*
import kotlinx.android.synthetic.main.include_failure.*
import kotlinx.android.synthetic.main.include_loading.*

class VideoFeedActivity : AppCompatActivity() {

    private val feedAdapter = VideoFeedAdapter()

    private val viewModel by lazy { ViewModelProviders.of(this).get<VideoFeedViewModel>() }

    private val stateGroups by lazy { listOf(loading, failure, content) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        list.adapter = feedAdapter
        list.layoutManager = LinearLayoutManager(this)
        list.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        refresh.setOnRefreshListener {
            viewModel.reload()
        }

        retryButton.setOnClickListener {
            viewModel.reload() // TODO: retry instead of reload
        }

        viewModel.state.observe(this, ::render)
    }

    private fun render(state: VideoFeedState) {
        if (feedAdapter.currentList != state.data) {
            feedAdapter.submitList(state.data)
        }
        when {
            state.loadState is Loading && state.data.isEmpty() -> stateGroups.showOnly(loading)
            state.loadState is Idle -> stateGroups.showOnly(content)
            state.loadState is Failure && state.data.isEmpty() -> stateGroups.showOnly(failure)
            state.loadState is Failure && state.data.isNotEmpty() -> {
                stateGroups.showOnly(content)
                showErrorSnackBar(state.loadState.exception)
            }
        }
        if (state.loadState is Failure) {
            Log.e(TAG, state.loadState.exception.message, state.loadState.exception)
        }
        refresh.isRefreshing = state.data.isNotEmpty() && state.loadState is Loading
    }

    private fun showErrorSnackBar(exception: Exception) {
        Snackbar.make(content, exception.message ?: "Unknown error", Snackbar.LENGTH_INDEFINITE)
            .setAction("Retry") { viewModel.reload() }
            .show()
    }

}

private val TAG = VideoFeedActivity::class.java.simpleName
