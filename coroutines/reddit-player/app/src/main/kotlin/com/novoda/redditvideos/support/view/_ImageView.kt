package com.novoda.redditvideos.support.view

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.novoda.redditvideos.model.PreviewUrl

fun ImageView.load(previewUrl: PreviewUrl) {
    Glide.with(this)
        .load(previewUrl.value)
        .into(this)
}
