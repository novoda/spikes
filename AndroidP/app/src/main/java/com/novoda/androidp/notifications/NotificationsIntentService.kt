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

class NotificationsIntentService : IntentService("NotificationsIntentService") {
    private val REPLY_ACTION = "REPLY_ACTION"
    private val KEY_NOTIFICATION_ID = "KEY_NOTIFICATION_ID"
    private val KEY_MESSAGE_ID = "KEY_MESSAGE_ID"

    val replyHistory = ArrayList<CharSequence>()

    override fun onHandleIntent(intent: Intent?) {
        val notificationManager = NotificationManagerCompat.from(this)
        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_delete)
                .setContentTitle(intent?.getStringExtra(EXTRA_TITLE))
                .setContentText(intent?.getStringExtra(EXTRA_TEXT))
                .setAutoCancel(true)

        val resultsFromIntent = RemoteInput.getResultsFromIntent(intent)
        if (resultsFromIntent != null) {
            val charSequence = resultsFromIntent.getCharSequence(KEY_TEXT_REPLY)
            replyHistory.add(charSequence)
        }
        notificationBuilder.setRemoteInputHistory(replyHistory.toTypedArray())


        addReplyAction(notificationBuilder)
        notificationManager.notify(1234, notificationBuilder.build())

    }

    private fun addReplyAction(notificationBuilder: NotificationCompat.Builder) {
        notificationBuilder.addAction(NotificationCompat.Action.Builder(R.drawable.ic_media_rew, "Reply", createReplyPendingIntent())
                .addRemoteInput(android.support.v4.app.RemoteInput.Builder(KEY_TEXT_REPLY)
                        .setLabel("Your inline response")
                        .build())
                .setAllowGeneratedReplies(true)
                .build())
    }

    private fun createReplyPendingIntent(): PendingIntent? {
        val intent = Intent(this, NotificationsIntentService::class.java).apply {
            action = ACTION_REPLY_NOTIFICATION
            putExtra(KEY_NOTIFICATION_ID, 1234)
            putExtra(KEY_MESSAGE_ID, 0)
        }

        return PendingIntent.getService(applicationContext, 100, intent,
                PendingIntent.FLAG_UPDATE_CURRENT)
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
