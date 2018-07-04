package com.novoda.sliceprovider

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.core.graphics.drawable.IconCompat
import androidx.slice.Slice
import androidx.slice.builders.ListBuilder
import androidx.slice.builders.ListBuilder.*
import androidx.slice.builders.SliceAction
import java.util.concurrent.TimeUnit

private val TIME_TO_LIVE = TimeUnit.SECONDS.toMillis(10)

internal fun createDemoSlice(context: Context, sliceUri: Uri): Slice =
    ListBuilder(context, sliceUri, TIME_TO_LIVE)
        .setHeader {
            it.setTitle("Header title")
                .setSubtitle("Header subtitle")
                .setSummary("This is the summary subtitle").primaryAction = sliceAction(context)
        }
        .addRow {
            it.setTitle("This is a row item title")
                .setSubtitle("...and this is the subtitle")
                .addEndItem(sliceAction(context, true))
        }
        .addInputRange {
            it.setTitle("This is an input range item")
                .setMax(7)
                .setValue(5).inputAction = pendingIntent(context, 86586)
        }
        .addRange {
            it.setTitle("This is a range item")
                .setMax(7).value = 2
        }
        .addRow { it.title = "This next item is a grid item:" }
        .addGridRow {
            it.addCell {
                it.addImage(IconCompat.createWithResource(context, R.drawable.ic_android), ICON_IMAGE)
                    .addText("Icon")
                    .addTitleText("Title text")
                    .contentIntent = pendingIntent(context, 8456)
            }
                .addCell {
                    it.addImage(IconCompat.createWithResource(context, R.drawable.yucca), SMALL_IMAGE)
                        .addText("Small")
                        .addTitleText("Title text")
                        .contentIntent = pendingIntent(context, 8964)
                }
                .addCell {
                    it.addImage(IconCompat.createWithResource(context, R.drawable.dianella), LARGE_IMAGE)
                        .addText("Large")
                        .addTitleText("Title text")
                        .contentIntent = pendingIntent(context, 6585)
                }
                .setSeeMoreCell {
                    it.addText("See more!")
                        .addTitleText("Title text")
                        .contentIntent = pendingIntent(context, 3287)
                }
        }
        .setSeeMoreRow { it.title = "See more row item️" }
        .addAction(sliceAction(context))
        .addAction(sliceAction2(context))
        .build()

private fun sliceAction(context: Context, isChecked: Boolean = false) = SliceAction(
    pendingIntent(context, 1234),
    IconCompat.createWithResource(context, R.drawable.ic_android),
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
    IconCompat.createWithResource(context, R.drawable.ic_android),
    "a lie"
)
