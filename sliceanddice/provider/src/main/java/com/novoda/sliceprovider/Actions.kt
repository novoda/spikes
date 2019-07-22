package com.novoda.sliceprovider

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.core.graphics.drawable.IconCompat
import androidx.slice.builders.ListBuilder
import androidx.slice.builders.SliceAction

fun sliceAction(context: Context): SliceAction = SliceAction.create(
    pendingIntent(context, 1234),
    IconCompat.createWithResource(context, R.drawable.ic_android),
    ListBuilder.ICON_IMAGE,
    "actionnnnnn"
)

fun pendingIntent(context: Context, reqCode: Int): PendingIntent {
    return PendingIntent.getActivity(
        context,
        reqCode,
        Intent(Settings.ACTION_SETTINGS),
        PendingIntent.FLAG_UPDATE_CURRENT
    )
}

fun sliceAction2(context: Context): SliceAction = SliceAction.create(
    PendingIntent.getActivity(
        context,
        546754,
        Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS),
        PendingIntent.FLAG_UPDATE_CURRENT
    ),
    IconCompat.createWithResource(context, R.drawable.ic_android),
    ListBuilder.ICON_IMAGE,
    "a lie"
)
