package com.novoda.sliceanddice

import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.content.pm.PackageManager.NameNotFoundException
import android.net.Uri
import androidx.appcompat.app.AlertDialog
import com.novoda.sliceshost.R

internal fun providerAppNotInstalled(packageManager: PackageManager, providerAuthority: String): Boolean {
    try {
        val packageInfo = packageManager.getPackageInfo(providerAuthority, PackageManager.GET_PROVIDERS)
            ?: return true

        val contentProvider = packageInfo.providers.find { it.authority == providerAuthority }
        return contentProvider == null
    } catch (e: NameNotFoundException) {
        return true
    }
}

fun showMissingProviderDialog(context: Context, onDismiss: () -> Unit, sliceUri: Uri) {
    AlertDialog.Builder(context, R.style.Theme_AppCompat_Light_Dialog_Alert)
        .setMessage("""Content provider not found: $sliceUri.

     • Does the URI start with content:// (e.g., for authority "com.example", it's content://com.example)?

     • Does the provider authority match its app's package name?

     • Have you installed the slice provider app first?
    """)
        .setPositiveButton("OK", { dialog: DialogInterface, _ -> dialog.dismiss() })
        .setOnDismissListener { onDismiss.invoke() }
        .show()
}
