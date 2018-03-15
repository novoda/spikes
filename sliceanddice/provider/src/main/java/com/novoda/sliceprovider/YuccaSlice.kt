package com.novoda.sliceprovider

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Icon
import android.net.Uri
import androidx.slice.Slice
import androidx.slice.builders.GridBuilder
import androidx.slice.builders.ListBuilder
import androidx.slice.builders.ListBuilder.HeaderBuilder
import androidx.slice.builders.ListBuilder.RowBuilder

@SuppressLint("RestrictedApi") // The GridBuilder's image size definitions shouldn't be restricted APIs
internal fun createYuccaSlice(context: Context, sliceUri: Uri): Slice = ListBuilder(context, sliceUri)
    .apply {
        setHeader(
            HeaderBuilder(this)
                .setTitle("Image search")
        )
        addRow(
            RowBuilder(this)
                .setTitle("This seems to be a match")
        )
        addGrid(
            GridBuilder(this).apply {
                paddingCell(context)
                paddingCell(context)
                addCell(GridBuilder.CellBuilder(this)
                    .addImage(Icon.createWithResource(context, R.drawable.pic), GridBuilder.LARGE_IMAGE)
                )
            }
        )
        addRow(
            RowBuilder(this)
                .setTitle("It might be one of these")
        )
        addGrid(
            GridBuilder(this).apply {
                addCell(GridBuilder.CellBuilder(this)
                    .addText("Yucca")
                    .addImage(Icon.createWithResource(context, R.drawable.yucca), GridBuilder.LARGE_IMAGE)
                )
                addCell(GridBuilder.CellBuilder(this)
                    .addTitleText("Dianella tasmanica")
                    .addImage(Icon.createWithResource(context, R.drawable.dianella), GridBuilder.LARGE_IMAGE)
                )
                paddingCell(context)
            }
        )
    }
    .build()

@SuppressLint("RestrictedApi") // The GridBuilder's image size definitions shouldn't be restricted APIs
private fun GridBuilder.paddingCell(context: Context) {
    // We cheat here — we use cells with a transparent cell and an empty title and text so that
    // we can trick the GridRowView into allocating a taller height (abc_slice_grid_max_height: 140dp)
    // than we would get if the items had only an image (abc_slice_grid_image_only_height: 86dp)
    // or an image and one text item (abc_slice_grid_image_text_height: 120dp)
    addCell(GridBuilder.CellBuilder(this)
        .addImage(Icon.createWithResource(context, R.drawable.transparent), GridBuilder.LARGE_IMAGE)
        .addTitleText("")
        .addText("")
    )
}
