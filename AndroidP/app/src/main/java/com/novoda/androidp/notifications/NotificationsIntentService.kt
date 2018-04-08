package com.novoda.androidp.notifications

import android.app.IntentService
import android.app.Notification
import android.app.PendingIntent
import android.app.RemoteInput
import android.content.Context
import android.content.Intent
import android.content.Intent.EXTRA_TEXT
import android.content.Intent.EXTRA_TITLE
import android.graphics.drawable.Icon
import android.support.v4.app.NotificationManagerCompat
import com.novoda.androidp.R
import java.util.*


private const val ACTION_SHOW_NOTIFICATION = "com.novoda.androidp.notifications.action.SHOW_NOTIFICATION"
private const val ACTION_REPLY_NOTIFICATION = "com.novoda.androidp.notifications.action.REPLY_NOTIFICATION"
private const val NOTIFICATION_ID = 1234
private val replyHistory = ArrayList<CharSequence>()

class Messages(val text: String, val time: Long, val sender: Notification.Person)

class NotificationsIntentService : IntentService("NotificationsIntentService") {
    private val KEY_NOTIFICATION_ID = "KEY_NOTIFICATION_ID"
    private val KEY_MESSAGE_ID = "KEY_MESSAGE_ID"

    override fun onHandleIntent(intent: Intent?) {
        val notificationManager = NotificationManagerCompat.from(this)
        val notificationBuilder = builderFromIntent(intent)

        addReplyHistory(notificationBuilder, intent)
        addReplyAction(notificationBuilder, intent)

        add_P_Images(notificationBuilder)

        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
    }

    private fun add_P_Images(notificationBuilder: Notification.Builder) {


        val senderOne = Notification.Person()
        senderOne.name = "Seb"
        senderOne.icon = Icon.createWithResource(this, R.drawable.sender_two)

        val senderTwo = Notification.Person()
        senderTwo.name = "Luis G. Valle"
        senderTwo.icon = Icon.createWithResource(this, R.drawable.sender_one)

        val messages = arrayOf(
                Messages("hi!", System.currentTimeMillis(), senderOne),
                Messages("still working in that spike?", System.currentTimeMillis()+100, senderTwo)
        )

        notificationBuilder.setStyle(Notification.MessagingStyle(senderOne)
                .addMessage(messages[0].text, messages[0].time, messages[0].sender)
                .addMessage(messages[1].text, messages[1].time, messages[1].sender))
    }

    private fun addReplyHistory(builder: Notification.Builder, intent: Intent?) {
        RemoteInput.getResultsFromIntent(intent)?.apply {
            replyHistory.add(0, getCharSequence(KEY_TEXT_REPLY)) // most recent notification should be at index 0
        }
        builder.setRemoteInputHistory(replyHistory.toTypedArray())
    }

    private fun builderFromIntent(intent: Intent?): Notification.Builder {
        val notificationBuilder = Notification.Builder(this, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_alert)
                .setContentTitle(intent?.getStringExtra(EXTRA_TITLE))
                .setContentText(intent?.getStringExtra(EXTRA_TEXT))
                .setAutoCancel(true)
        return notificationBuilder
    }

    private fun addReplyAction(notificationBuilder: Notification.Builder, intent: Intent?) {
        notificationBuilder.addAction(Notification.Action.Builder(R.drawable.ic_reply_black_24dp, "Reply", createReplyPendingIntent(intent))
                .addRemoteInput(RemoteInput.Builder(KEY_TEXT_REPLY)
                        .setLabel("Reply...")
                        .build())
                .setAllowGeneratedReplies(true)
                .build())
    }

    private fun createReplyPendingIntent(intent: Intent?): PendingIntent? {
        val replyIntent = Intent(this, NotificationsIntentService::class.java).apply {
            action = ACTION_REPLY_NOTIFICATION
            putExtra(KEY_NOTIFICATION_ID, NOTIFICATION_ID)
            putExtra(KEY_MESSAGE_ID, 0)
            putExtra(EXTRA_TITLE, intent?.getStringExtra(EXTRA_TITLE)) // reply will include original title and text
            putExtra(EXTRA_TEXT, intent?.getStringExtra(EXTRA_TEXT))
        }

        return PendingIntent.getService(applicationContext, 100, replyIntent, PendingIntent.FLAG_UPDATE_CURRENT)
    }


    companion object {
        fun showNotification(context: Context, title: String, body: String) {
            val intent = Intent(context, NotificationsIntentService::class.java).apply {
                action = ACTION_SHOW_NOTIFICATION
                putExtra(EXTRA_TITLE, title)
                putExtra(EXTRA_TEXT, body)
            }
            context.startService(intent)
        }
    }

}
