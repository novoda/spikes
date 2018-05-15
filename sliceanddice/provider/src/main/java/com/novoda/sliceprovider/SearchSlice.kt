package com.novoda.sliceprovider

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.Html
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import androidx.core.graphics.drawable.IconCompat
import androidx.slice.Slice
import androidx.slice.builders.GridRowBuilder
import androidx.slice.builders.ListBuilder
import androidx.slice.builders.ListBuilder.HeaderBuilder
import androidx.slice.builders.ListBuilder.ICON_IMAGE
import androidx.slice.builders.ListBuilder.LARGE_IMAGE
import androidx.slice.builders.ListBuilder.RowBuilder
import java.util.concurrent.TimeUnit

private val TIME_TO_LIVE = TimeUnit.SECONDS.toMillis(10)

internal fun createSearchSlice(context: Context, sliceUri: Uri): Slice = ListBuilder(context, sliceUri, TIME_TO_LIVE)
    .apply {
        setHeader(
            HeaderBuilder(this)
                .setTitle("What's up with Rick and Morty")
        )
        addRow(
            RowBuilder(this)
                .setTitle("Here are some results. The first is from Reddit")
                .setTitleItem(IconCompat.createWithResource(context, R.drawable.assistant), ICON_IMAGE)
        )
        addGridRow(
            GridRowBuilder(this).apply {
                addCell(GridRowBuilder.CellBuilder(this)
                    .addTitleText("What's the deal with Rick and Morty? : OutOfTheLoop - Reddit")
                    .addText("https://www.reddit.com › comments › what-s-the-deal-with-rick-and-morty".asLink())
                    .setContentIntent(
                        webPendingIntent(
                            context,
                            "https://www.reddit.com/r/OutOfTheLoop/comments/6530s8/whats_the_deal_with_rick_and_morty/"
                        )
                    )
                )
                addCell(GridRowBuilder.CellBuilder(this)
                    .addTitleText("Rick and Morty - Wikipedia")
                    .addText("https://en.m.wikipedia.org › wiki › Rick_and_Morty")
                    .setContentIntent(
                        webPendingIntent(
                            context,
                            "https://en.wikipedia.org/wiki/Rick_and_Morty"
                        )
                    )
                )
                addCell(GridRowBuilder.CellBuilder(this)
                    .addTitleText("Why 'Rick and Morty' is the realest show on TV right now - Mashable")
                    .addText("https://mashable.com › wiki › Rick_and_Morty".asLink())
                    .setContentIntent(
                        webPendingIntent(
                            context,
                            "https://mashable.com/2017/09/29/rick-morty-season-3-finale-dan-harmon-interview-humanity-real/"
                        )
                    )
                )
            }
        )
        addRow(
            RowBuilder(this)
                .setTitle("Who animates Rick and Morty?")
        )
        addRow(
            RowBuilder(this)
                .setTitle("Here's something from Wikipedia")
                .setTitleItem(IconCompat.createWithResource(context, R.drawable.assistant), ICON_IMAGE)
        )
        addGridRow(
            GridRowBuilder(this).apply {
                addCell(GridRowBuilder.CellBuilder(this)
                    .addImage(IconCompat.createWithResource(context, R.drawable.rick_and_morty), LARGE_IMAGE)
                    .addText(("<b>Rick and Morty</b> is an American adult animated science fiction comedy series created by " +
                        "<b>Justin Roiland</b> and <b>Dan Harmon</b> for Cartoon Network's late-night programming " +
                        "block <b>Adult Swim</b>.").asHtml())
                    .setContentIntent(
                        webPendingIntent(
                            context,
                            "https://en.wikipedia.org/wiki/Rick_and_Morty"
                        )
                    )
                )
            }
        )
    }
    .build()

private fun webPendingIntent(context: Context, url: String) =
    PendingIntent.getActivity(
        context,
        0,
        Intent(Intent.ACTION_VIEW).apply { data = Uri.parse(url) },
        0
    )

private fun String.asLink(): CharSequence =
    SpannableStringBuilder(this).apply {
        setSpan(ForegroundColorSpan(0xFF2B662A.toInt()), 0, this.length, SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE)
    }

private fun String.asHtml(): CharSequence =
    Html.fromHtml(this, Html.FROM_HTML_MODE_LEGACY)
