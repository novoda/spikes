package com.novoda.sliceanddice

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.util.Log
import androidx.slice.Slice

@SuppressLint("InlinedApi") // It's fine, the constants are used by the support library
fun Slice.isPermissionRequest(appName: String): Boolean {
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

val Slice.permissionRequestPendingIntent: PendingIntent
    get() = try {
        items[0].action
    } catch (e: ClassCastException) {
        Log.e("Slice&Dice", "Trying to extract permission request pending intent from a slice which is not a permission request: $this")
        throw IllegalArgumentException("The slice '$this' is not a valid permission request slice.")
    }
