package com.novoda.redditvideos

import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.get
import androidx.recyclerview.widget.RecyclerView
import com.novoda.redditvideos.VideoFeedState.*
import com.novoda.redditvideos.model.Video
import com.novoda.redditvideos.support.view.inflateDetached
import com.novoda.redditvideos.support.view.load
import com.novoda.redditvideos.support.view.revealDelayed
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_video_entry.*
import kotlinx.android.synthetic.main.state_failure.*
import kotlinx.android.synthetic.main.state_loading.*

internal class VideoFeedAdapter : RecyclerView.Adapter<VideoFeedViewHolder>() {

    var state: VideoFeedState = Loading
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, position: Int) = when (state) {
        is Loading -> LoadingViewHolder(parent)
        is Idle, is VideoFeedState.LoadingWithCache -> VideoItemViewHolder(parent)
        is Failure -> FailureViewHolder(parent)
    }

    override fun getItemCount() = (state as? VideoFeedState.HasVideos)?.videos?.size ?: 1

    override fun onBindViewHolder(viewHolder: VideoFeedViewHolder, position: Int) = when (viewHolder) {
        is VideoItemViewHolder -> viewHolder.bind((state as VideoFeedState.HasVideos).videos[position])
        is LoadingViewHolder -> viewHolder.bind()
        is FailureViewHolder -> viewHolder.bind(state as Failure)
    }

    override fun getItemViewType(position: Int) = when (state) {
        is Loading -> 0
        is Idle, is VideoFeedState.LoadingWithCache -> 1
        is Failure -> 2
    }

}

sealed class VideoFeedViewHolder(
    parent: ViewGroup,
    @LayoutRes layoutId: Int,
    override val containerView: View = parent.inflateDetached(layoutId)
) : RecyclerView.ViewHolder(containerView), LayoutContainer

private class LoadingViewHolder(parent: ViewGroup) : VideoFeedViewHolder(parent, R.layout.state_loading)
private class VideoItemViewHolder(parent: ViewGroup) : VideoFeedViewHolder(parent, R.layout.item_video_entry)
private class FailureViewHolder(parent: ViewGroup) : VideoFeedViewHolder(parent, R.layout.state_failure)


private fun VideoItemViewHolder.bind(video: Video) {
    title.text = video.title
    thumbnail.load(video.thumbnail)
}

private fun FailureViewHolder.bind(failure: Failure) {
    errorMessage.text = failure.message
    retryButton.setOnClickListener {
        viewModel.reload()
    }
}

private fun LoadingViewHolder.bind() {
    progressBar.revealDelayed()
}

private val VideoFeedViewHolder.viewModel
    get() = ViewModelProviders.of(itemView.context as FragmentActivity).get<VideoFeedViewModel>()
