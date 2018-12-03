package com.novoda.redditvideos.support.view

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.novoda.redditvideos.model.Thumbnail

fun ImageView.load(thumbnail: Thumbnail) {
    Glide.with(this)
        .load(thumbnail.value)
        .into(this)
}
