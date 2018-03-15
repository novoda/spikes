package com.novoda.sliceprovider

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Icon
import android.net.Uri
import android.provider.Settings
import androidx.slice.Slice
import androidx.slice.builders.GridBuilder
import androidx.slice.builders.ListBuilder
import androidx.slice.builders.ListBuilder.HeaderBuilder
import androidx.slice.builders.ListBuilder.InputRangeBuilder
import androidx.slice.builders.ListBuilder.RangeBuilder
import androidx.slice.builders.ListBuilder.RowBuilder
import androidx.slice.builders.SliceAction

@SuppressLint("RestrictedApi") // The GridBuilder's image size definitions shouldn't be restricted APIs
internal fun createDemoSlice(context: Context, sliceUri: Uri): Slice = ListBuilder(context, sliceUri)
    .apply {
        setHeader(
            HeaderBuilder(this)
                .setTitle("Header title")
                .setSubtitle("Header subtitle")
                .setSummarySubtitle("This is the summary subtitle")
                .setPrimaryAction(sliceAction(context))
        )
    }
    .addRow(RowBuilder(context, sliceUri)
        .setTitle("Banana \uD83C\uDF4C")
        .setSubtitle("I am a slice!")
        .setTitleItem(Icon.createWithResource(context, R.drawable.ic_sun))
        .addEndItem(sliceAction(context, true))
    )
    .apply {
        addInputRange(
            InputRangeBuilder(this)
                .setTitle("Input range")
                .setMax(7)
                .setAction(pendingIntent(context, 86586))
        )
        addRange(
            RangeBuilder(this)
                .setTitle("Range")
                .setMax(7)
                .setValue(2)
        )
        addGrid(
            GridBuilder(this)
                .apply {
                    addCell(
                        GridBuilder.CellBuilder(this)
                            .addImage(Icon.createWithResource(context, R.drawable.ic_android), GridBuilder.ICON_IMAGE)
                            .addText("Icon")
                            .addTitleText("Title text")
                            .setContentIntent(pendingIntent(context, 8456))
                    )
                    addCell(
                        GridBuilder.CellBuilder(this)
                            .addImage(Icon.createWithResource(context, R.drawable.ic_android), GridBuilder.SMALL_IMAGE)
                            .addText("Small")
                            .addTitleText("Title text")
                            .setContentIntent(pendingIntent(context, 8964))
                    )
                    addCell(
                        GridBuilder.CellBuilder(this)
                            .addImage(Icon.createWithResource(context, R.drawable.ic_android), GridBuilder.LARGE_IMAGE)
                            .addText("Large")
                            .addTitleText("Title text")
                            .setContentIntent(pendingIntent(context, 6585))
                    )
                    addSeeMoreCell(
                        GridBuilder.CellBuilder(this)
                            .addText("See more")
                            .addTitleText("Title text")
                            .setContentIntent(pendingIntent(context, 3287))
                    )
                }
        )
    }
    .addSeeMoreRow(RowBuilder(context, sliceUri)
        .setTitle("See more ➡️")
        .addEndItem(sliceAction2(context)))
    .addAction(sliceAction(context))
    .addAction(sliceAction2(context))
    .build()

private fun sliceAction(context: Context, isChecked: Boolean = false) = SliceAction(
    pendingIntent(context, 1234),
    Icon.createWithResource(context, R.drawable.ic_android),
    "actionnnnnn",
    isChecked
)

private fun pendingIntent(context: Context, reqCode: Int): PendingIntent {
    return PendingIntent.getActivity(
        context,
        reqCode,
        Intent(Settings.ACTION_SETTINGS),
        PendingIntent.FLAG_UPDATE_CURRENT
    )
}

private fun sliceAction2(context: Context) = SliceAction(
    PendingIntent.getActivity(
        context,
        546754,
        Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS),
        PendingIntent.FLAG_UPDATE_CURRENT
    ),
    Icon.createWithResource(context, R.drawable.ic_android),
    "a lie"
)
