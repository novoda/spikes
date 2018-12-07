package com.novoda.redditvideos.support.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes

fun ViewGroup.inflateDetached(@LayoutRes layoutId: Int): View =
    LayoutInflater.from(context).inflate(layoutId, this, false)

fun List<View>.showOnly(vararg views: View) = forEach { view ->
    view.visibility = if (views.contains(view)) View.VISIBLE else View.GONE
}
