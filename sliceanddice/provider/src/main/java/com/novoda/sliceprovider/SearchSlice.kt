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
import androidx.slice.builders.ListBuilder
import androidx.slice.builders.ListBuilder.ICON_IMAGE
import androidx.slice.builders.ListBuilder.LARGE_IMAGE
import java.util.concurrent.TimeUnit

private val TIME_TO_LIVE = TimeUnit.SECONDS.toMillis(10)

internal fun createSearchSlice(context: Context, sliceUri: Uri): Slice =
    ListBuilder(context, sliceUri, TIME_TO_LIVE)
        .setHeader { it.title = "What's up with Rick and Morty" }
        .addRow {
            it.setTitle("Here are some results. The first is from Reddit")
                .setTitleItem(IconCompat.createWithResource(context, R.drawable.assistant), ICON_IMAGE)
        }
        .addGridRow {
            it
                .addCell {
                    it.addTitleText("What's the deal with Rick and Morty? : OutOfTheLoop - Reddit")
                        .addText("https://www.reddit.com › comments › what-s-the-deal-with-rick-and-morty".asLink())
                        .contentIntent = webPendingIntent(
                        context,
                        "https://www.reddit.com/r/OutOfTheLoop/comments/6530s8/whats_the_deal_with_rick_and_morty/"
                    )
                }
                .addCell {
                    it.addTitleText("Rick and Morty - Wikipedia")
                        .addText("https://en.m.wikipedia.org › wiki › Rick_and_Morty")
                        .contentIntent = webPendingIntent(
                        context,
                        "https://en.wikipedia.org/wiki/Rick_and_Morty"
                    )
                }
                .addCell {
                    it.addTitleText("Why 'Rick and Morty' is the realest show on TV right now - Mashable")
                        .addText("https://mashable.com › wiki › Rick_and_Morty".asLink())
                        .contentIntent = webPendingIntent(
                        context,
                        "https://mashable.com/2017/09/29/rick-morty-season-3-finale-dan-harmon-interview-humanity-real/"
                    )
                }
        }
        .addRow { it.title = "Who animates Rick and Morty?" }
        .addRow {
            it.setTitle("Here's something from Wikipedia")
                .setTitleItem(IconCompat.createWithResource(context, R.drawable.assistant), ICON_IMAGE)
        }
        .addGridRow {
            it.addCell {
                val itemText = "<b>Rick and Morty</b> is an American adult animated science fiction comedy series " +
                    "created by <b>Justin Roiland</b> and <b>Dan Harmon</b> for Cartoon Network's late-night " +
                    "programming block <b>Adult Swim</b>."
                        .asHtml()

                it.addImage(IconCompat.createWithResource(context, R.drawable.rick_and_morty), LARGE_IMAGE)
                    .addText(itemText)
                    .contentIntent = webPendingIntent(context, "https://en.wikipedia.org/wiki/Rick_and_Morty")
            }
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
