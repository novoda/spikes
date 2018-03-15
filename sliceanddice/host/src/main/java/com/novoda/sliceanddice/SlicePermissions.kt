package com.novoda.sliceanddice

import android.app.Activity
import android.content.IntentSender
import android.net.Uri
import android.widget.Toast
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
