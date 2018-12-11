package com.novoda.redditvideos

import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.novoda.redditvideos.model.Video
import com.novoda.redditvideos.support.view.inflateDetached
import com.novoda.redditvideos.support.view.load
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_video_entry.*

private val diffCallback = object : DiffUtil.ItemCallback<Video>() {
    override fun areItemsTheSame(oldItem: Video, newItem: Video) = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: Video, newItem: Video) = oldItem == newItem
}

internal class VideoFeedAdapter : PagedListAdapter<Video, VideoFeedViewHolder>(diffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, position: Int) =
        VideoFeedViewHolder(parent)

    override fun onBindViewHolder(viewHolder: VideoFeedViewHolder, position: Int) =
        viewHolder.bind(getItem(position)!!)

}

class VideoFeedViewHolder(
    parent: ViewGroup,
    override val containerView: View = parent.inflateDetached(R.layout.item_video_entry)
) : RecyclerView.ViewHolder(containerView), LayoutContainer

private fun VideoFeedViewHolder.bind(video: Video) {
    title.text = video.title
    thumbnail.load(video.previewUrl)
}
