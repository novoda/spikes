package com.novoda.sliceanddice

import android.annotation.SuppressLint
import android.app.Activity
import android.app.PendingIntent
import android.content.IntentSender
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.slice.Slice
import androidx.slice.SliceManager

internal fun SliceManager.missingPermission(sliceUri: Uri, appName: String) = try {
    // TODO this is a very expensive and hacky way to check, but we don't really have alternatives
    // as Android P DP1, since there's no permission API 🤷‍
    bindSlice(sliceUri)?.isPermissionRequest(appName) ?: false
} catch (e: SecurityException) {
    true
} catch (e: IllegalArgumentException) {
    false
}

@SuppressLint("InlinedApi") // It's fine, the constants are used by the support library
private fun Slice.isPermissionRequest(appName: String): Boolean {
    // Permission slices have only one item, whose action is a PendingIntent to
    // show the permission granting activity (which shows a dialog). We cannot
    // easily check the contents of the action PendingIntent, so for now we just
    // assume that if it's not null it's good enough. We'll check other signals
    // afterwards to verify if it's really a permission slice.
    if (items.count() != 1 || items[0].action == null) {
        return false
    }

    // Permission slices contain one slice item, which contains a slice item which is
    // a FORMAT_TEXT with a fixed format
    val subItems = items.first().slice.items
    val firstSubItem = subItems.firstOrNull()
    return firstSubItem?.format == android.app.slice.SliceItem.FORMAT_TEXT &&
        firstSubItem.text == "$appName wants to show Slice Provider slices"
}

internal fun SliceManager.requestPermission(sliceUri: Uri, activity: Activity) {
    try {
        val intentSender: IntentSender? = bindSlice(sliceUri)!!.permissionRequestPendingIntent.intentSender
        activity.startIntentSenderForResult(intentSender, REQUEST_CODE_PERMISSIONS, null, 0, 0, 0)
    } catch (e: IllegalArgumentException) {
        Toast.makeText(
            activity,
            "Looks like we've hit a bug in Slices, fixed in alpha2. " +
                "Uninstall the app and reboot the device/emulator.",
            Toast.LENGTH_LONG
        ).show()
    }
}

private val Slice.permissionRequestPendingIntent: PendingIntent
    get() = try {
        items[0].action
    } catch (e: ClassCastException) {
        Log.e("Slice&Dice", "Trying to extract permission request pending intent from a slice which is not a permission request: $this")
        throw IllegalArgumentException("The slice '$this' is not a valid permission request slice.")
    }

internal inline fun handleSlicePermissionActivityResult(
    requestCode: Int,
    onSlicePermissionResult: () -> Unit,
    onUnhandledActivityResult: () -> Unit = {}
) {
    if (requestCode == REQUEST_CODE_PERMISSIONS) {
        onSlicePermissionResult.invoke()
    } else {
        onUnhandledActivityResult.invoke()
    }
}

private const val REQUEST_CODE_PERMISSIONS = 1234
