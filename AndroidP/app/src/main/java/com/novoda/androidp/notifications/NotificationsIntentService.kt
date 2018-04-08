package com.novoda.androidp.notifications

import android.R
import android.app.IntentService
import android.app.PendingIntent
import android.app.RemoteInput
import android.content.Context
import android.content.Intent
import android.content.Intent.EXTRA_TEXT
import android.content.Intent.EXTRA_TITLE
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat


private const val ACTION_SHOW_NOTIFICATION = "com.novoda.androidp.notifications.action.SHOW_NOTIFICATION"
private const val ACTION_REPLY_NOTIFICATION = "com.novoda.androidp.notifications.action.REPLY_NOTIFICATION"
private const val NOTIFICATION_ID = 1234
private val replyHistory = ArrayList<CharSequence>()

class NotificationsIntentService : IntentService("NotificationsIntentService") {
    private val KEY_NOTIFICATION_ID = "KEY_NOTIFICATION_ID"
    private val KEY_MESSAGE_ID = "KEY_MESSAGE_ID"

    override fun onHandleIntent(intent: Intent?) {
        val notificationManager = NotificationManagerCompat.from(this)
        val notificationBuilder = builderFromIntent(intent)

        addReplyHistory(notificationBuilder, intent)
        addReplyAction(notificationBuilder, intent)

        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
    }

    private fun addReplyHistory(builder: NotificationCompat.Builder, intent: Intent?) {
        RemoteInput.getResultsFromIntent(intent)?.apply {
            replyHistory.add(0, getCharSequence(KEY_TEXT_REPLY)) // most recent notification should be at index 0
        }
        builder.setRemoteInputHistory(replyHistory.toTypedArray())
    }

    private fun builderFromIntent(intent: Intent?): NotificationCompat.Builder {
        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_delete)
                .setContentTitle(intent?.getStringExtra(EXTRA_TITLE))
                .setContentText(intent?.getStringExtra(EXTRA_TEXT))
                .setAutoCancel(true)
        return notificationBuilder
    }

    private fun addReplyAction(notificationBuilder: NotificationCompat.Builder, intent: Intent?) {
        notificationBuilder.addAction(NotificationCompat.Action.Builder(R.drawable.ic_media_rew, "Reply", createReplyPendingIntent(intent))
                .addRemoteInput(android.support.v4.app.RemoteInput.Builder(KEY_TEXT_REPLY)
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
            putExtra(EXTRA_TITLE, intent?.getStringExtra(EXTRA_TITLE))
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
