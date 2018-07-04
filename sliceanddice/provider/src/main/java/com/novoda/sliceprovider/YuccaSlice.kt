package com.novoda.sliceprovider

import android.content.Context
import android.net.Uri
import androidx.core.graphics.drawable.IconCompat
import androidx.slice.Slice
import androidx.slice.builders.GridRowBuilder
import androidx.slice.builders.ListBuilder
import java.util.concurrent.TimeUnit

private val TIME_TO_LIVE = TimeUnit.SECONDS.toMillis(10)

internal fun createYuccaSlice(context: Context, sliceUri: Uri): Slice =
    ListBuilder(context, sliceUri, TIME_TO_LIVE)
        .setHeader { it.title = "Image search" }
        .addRow {
            it.title = "This seems like a match"
            it.setTitleItem(IconCompat.createWithResource(context, R.drawable.assistant), ListBuilder.ICON_IMAGE)
        }
        .addGridRow {
            it.addPaddingCell(context)
                .addPaddingCell(context)
                .addCell {
                    it.addImage(IconCompat.createWithResource(context, R.drawable.pic), ListBuilder.LARGE_IMAGE)
                }
        }
        .addRow {
            it.title = "It might be one of these"
            it.setTitleItem(IconCompat.createWithResource(context, R.drawable.assistant), ListBuilder.ICON_IMAGE)
        }
        .addGridRow {
            it
                .addCell {
                    it.addTitleText("Yucca")
                        .addImage(IconCompat.createWithResource(context, R.drawable.yucca), ListBuilder.LARGE_IMAGE)
                }
                .addCell {
                    it.addTitleText("Dianella tasmanica")
                        .addImage(IconCompat.createWithResource(context, R.drawable.dianella), ListBuilder.LARGE_IMAGE)
                }
                .addPaddingCell(context)
        }
        .build()

private fun GridRowBuilder.addPaddingCell(context: Context): GridRowBuilder {
    // We cheat here — we use cells with a transparent cell and an empty title and text so that
    // we can trick the GridRowView into allocating a taller height (abc_slice_grid_max_height: 140dp)
    // than we would get if the items had only an image (abc_slice_grid_image_only_height: 86dp)
    // or an image and one text item (abc_slice_grid_image_text_height: 120dp)
    return addCell {
        it.addImage(IconCompat.createWithResource(context, R.drawable.transparent), ListBuilder.LARGE_IMAGE)
            .addTitleText("")
            .addText("")
    }
}
