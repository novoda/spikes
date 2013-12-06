New Kit Kat SMS APIs
===
In order for your app to appear in the system settings as an eligible default SMS app, your manifest file must declare some specific capabilities. So you must update your app to do the following things:

 - In a broadcast receiver, include an intent filter for `SMS_DELIVER_ACTION ("android.provider.Telephony.SMS_DELIVER")`. The broadcast receiver must also require the `BROADCAST_SMS` permission.
This allows your app to directly receive incoming SMS messages.
 - In a broadcast receiver, include an intent filter for `WAP_PUSH_DELIVER_ACTION ("android.provider.Telephony.WAP_PUSH_DELIVER")` with the MIME type `"application/vnd.wap.mms-message"`. The broadcast receiver must also require the `BROADCAST_WAP_PUSH` permission.
This allows your app to directly receive incoming MMS messages.
 - In your activity that delivers new messages, include an intent filter for `ACTION_SENDTO ("android.intent.action.SENDTO")` with schemas, `sms:, smsto:, mms:, and mmsto:`.
 - This allows your app to receive intents from other apps that want to deliver a message.
In a service, include an intent filter for `ACTION_RESPONSE_VIA_MESSAGE ("android.intent.action.RESPOND_VIA_MESSAGE")` with schemas, `sms:, smsto:, mms:, and mmsto:`. This service must also require the `SEND_RESPOND_VIA_MESSAGE` permission.

See more at: http://android-developers.blogspot.co.uk/2013/10/getting-your-sms-apps-ready-for-kitkat.html