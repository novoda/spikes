package com.novoda.sliceprovider

import android.content.Context
import android.net.Uri
import androidx.core.graphics.drawable.IconCompat
import androidx.slice.Slice
import androidx.slice.builders.GridRowBuilder
import androidx.slice.builders.ListBuilder
import androidx.slice.builders.ListBuilder.HeaderBuilder
import androidx.slice.builders.ListBuilder.RowBuilder
import java.util.concurrent.TimeUnit

private val TIME_TO_LIVE = TimeUnit.SECONDS.toMillis(10)

internal fun createYuccaSlice(context: Context, sliceUri: Uri): Slice = ListBuilder(context, sliceUri, TIME_TO_LIVE)
    .apply {
        setHeader(
            HeaderBuilder(this)
                .setTitle("Image search")
        )
        addRow(
            RowBuilder(this)
                .setTitle("This seems like a match")
                .setTitleItem(IconCompat.createWithResource(context, R.drawable.assistant), ListBuilder.ICON_IMAGE)
        )
        addGridRow(
            GridRowBuilder(this).apply {
                paddingCell(context)
                paddingCell(context)
                addCell(GridRowBuilder.CellBuilder(this)
                    .addImage(IconCompat.createWithResource(context, R.drawable.pic), ListBuilder.LARGE_IMAGE)
                )
            }
        )
        addRow(
            RowBuilder(this)
                .setTitle("It might be one of these")
                .setTitleItem(IconCompat.createWithResource(context, R.drawable.assistant), ListBuilder.ICON_IMAGE)
        )
        addGridRow(
            GridRowBuilder(this).apply {
                addCell(GridRowBuilder.CellBuilder(this)
                    .addTitleText("Yucca")
                    .addImage(IconCompat.createWithResource(context, R.drawable.yucca), ListBuilder.LARGE_IMAGE)
                )
                addCell(GridRowBuilder.CellBuilder(this)
                    .addTitleText("Dianella tasmanica")
                    .addImage(IconCompat.createWithResource(context, R.drawable.dianella), ListBuilder.LARGE_IMAGE)
                )
                paddingCell(context)
            }
        )
    }
    .build()

private fun GridRowBuilder.paddingCell(context: Context) {
    // We cheat here — we use cells with a transparent cell and an empty title and text so that
    // we can trick the GridRowView into allocating a taller height (abc_slice_grid_max_height: 140dp)
    // than we would get if the items had only an image (abc_slice_grid_image_only_height: 86dp)
    // or an image and one text item (abc_slice_grid_image_text_height: 120dp)
    addCell(GridRowBuilder.CellBuilder(this)
        .addImage(IconCompat.createWithResource(context, R.drawable.transparent), ListBuilder.LARGE_IMAGE)
        .addTitleText("")
        .addText("")
    )
}
