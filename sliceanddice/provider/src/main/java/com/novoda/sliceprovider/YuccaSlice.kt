package com.novoda.sliceprovider

import android.content.Context
import android.net.Uri
import androidx.core.graphics.drawable.IconCompat
import androidx.slice.Slice
import androidx.slice.builders.*
import java.util.concurrent.TimeUnit

private val TIME_TO_LIVE = TimeUnit.SECONDS.toMillis(10)

internal fun createYuccaSlice(context: Context, sliceUri: Uri): Slice = list(context, sliceUri, TIME_TO_LIVE) {
    header {
        title = "Image search"
        primaryAction = sliceAction(context)
    }
    row {
        title = "This seems like a match"
        setTitleItem(IconCompat.createWithResource(context, R.drawable.assistant), ListBuilder.ICON_IMAGE)
    }
    gridRow {
        paddingCell(context)
        paddingCell(context)
        cell {
            addImage(IconCompat.createWithResource(context, R.drawable.pic), ListBuilder.LARGE_IMAGE)
        }
    }
    row {
        title = "It might be one of these"
        setTitleItem(IconCompat.createWithResource(context, R.drawable.assistant), ListBuilder.ICON_IMAGE)
    }
    gridRow {
        cell {
            addTitleText("Yucca")
            addImage(IconCompat.createWithResource(context, R.drawable.yucca), ListBuilder.LARGE_IMAGE)
        }
        cell {
            addTitleText("Dianella tasmanica")
            addImage(IconCompat.createWithResource(context, R.drawable.dianella), ListBuilder.LARGE_IMAGE)
        }
        paddingCell(context)
    }
}

private const val EMPTY_STRING = ""

// We cheat here — we use cells with a transparent cell and an empty title and text so that
// we can trick the GridRowView into allocating a taller height (abc_slice_grid_max_height: 140dp)
// than we would get if the items had only an image (abc_slice_grid_image_only_height: 86dp)
// or an image and one text item (abc_slice_grid_image_text_height: 120dp)
fun GridRowBuilderDsl.paddingCell(context: Context) = cell {
    addImage(IconCompat.createWithResource(context, R.drawable.transparent), ListBuilder.LARGE_IMAGE)
        .addTitleText(EMPTY_STRING)
        .addText(EMPTY_STRING)
}
