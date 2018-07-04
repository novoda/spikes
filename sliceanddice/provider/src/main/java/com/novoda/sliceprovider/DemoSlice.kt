package com.novoda.sliceprovider

import android.content.Context
import android.net.Uri
import androidx.core.graphics.drawable.IconCompat
import androidx.slice.Slice
import androidx.slice.builders.*
import androidx.slice.builders.ListBuilder.*
import java.util.concurrent.TimeUnit

private val TIME_TO_LIVE = TimeUnit.SECONDS.toMillis(10)

internal fun createDemoSlice(context: Context, sliceUri: Uri): Slice = list(context, sliceUri, TIME_TO_LIVE) {
    header {
        title = "Header title"
        subtitle = "Header subtitle"
        summary = "This is the summary subtitle"
        primaryAction = sliceAction(context)
    }
    row {
        title = "This is a row item title"
        subtitle = "...and this is the subtitle"
        addEndItem(sliceAction(context))
    }
    inputRange {
        title = "This is an input range item"
        max = 7
        value = 5
        inputAction = pendingIntent(context, 86586)
    }
    range {
        title = "This is a range item"
        max = 7
        value = 2
    }
    row { title = "This next item is a grid item:" }
    gridRow {
        cell {
            addImage(IconCompat.createWithResource(context, R.drawable.ic_android), ICON_IMAGE)
            addText("Icon")
            addTitleText("Title text")
            contentIntent = pendingIntent(context, 8456)
        }
        cell {
            addImage(IconCompat.createWithResource(context, R.drawable.yucca), SMALL_IMAGE)
            addText("Small")
            addTitleText("Title text")
            contentIntent = pendingIntent(context, 8964)
        }
        cell {
            addImage(IconCompat.createWithResource(context, R.drawable.dianella), LARGE_IMAGE)
            addText("Large")
            addTitleText("Title text")
            contentIntent = pendingIntent(context, 6585)
        }
        seeMoreCell {
            addText("See more!")
            addTitleText("Title text")
            contentIntent = pendingIntent(context, 3287)
        }
    }
    seeMoreRow { title = "See more row item️" }
    addAction(sliceAction(context))
    addAction(sliceAction2(context))
}
