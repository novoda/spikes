package com.novoda.sliceanddice

import android.content.Context
import android.content.DialogInterface
import android.net.Uri
import android.support.v7.app.AlertDialog
import com.novoda.sliceshost.R

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
