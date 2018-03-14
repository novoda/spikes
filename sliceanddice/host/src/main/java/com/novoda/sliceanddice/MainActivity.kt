package com.novoda.sliceanddice

import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.transition.TransitionManager
import android.view.View
import androidx.slice.SliceManager
import com.novoda.sliceshost.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var sliceManager: SliceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sliceManager = SliceManager.getInstance(this)
    }

    private val sliceUri = Uri.parse("content://com.novoda.sliceprovider/")

    override fun onStart() {
        super.onStart()

        if (providerAppNotInstalled()) {
            showMissingProviderDialog(this, { finish() }, sliceUri)
            return
        }

        tryShowingSlice()
    }

    private fun providerAppNotInstalled(): Boolean {
        try {
            val packageInfo = packageManager.getPackageInfo(sliceUri.authority, PackageManager.GET_PROVIDERS)
                    ?: return true

            val contentProvider = packageInfo.providers.find { it.authority == sliceUri.authority }
            return contentProvider == null
        } catch (e: PackageManager.NameNotFoundException) {
            return true
        }
    }

    private fun tryShowingSlice() {
        if (sliceManager.missingPermission()) {
            needPermissionGroup.visibility = View.VISIBLE
            sliceView.visibility = View.INVISIBLE
            permissionCta.setOnClickListener { sliceManager.requestPermission() }
        } else {
            startObservingSliceLiveData()
        }
    }

    private fun SliceManager.missingPermission() = try {
        // TODO this is a very expensive and hacky way to check, but we don't really have alternatives
        // as Android P DP1, since there's no permission API 🤷‍
        bindSlice(sliceUri)?.isPermissionRequest(getString(R.string.app_name)) ?: false
    } catch (e: SecurityException) {
        true
    } catch (e: IllegalArgumentException) {
        false
    }

    private fun SliceManager.requestPermission() {
        try {
            val intentSender: IntentSender? = bindSlice(sliceUri)!!.permissionRequestPendingIntent.intentSender
            startIntentSenderForResult(intentSender, REQUEST_CODE_PERMISSIONS, null, 0, 0, 0)
        } catch (e: IllegalArgumentException) {
            Snackbar.make(
                    root,
                    "Looks like we've hit a bug in Slices, fixed in alpha2. " +
                            "Uninstall the app and reboot the device/emulator.",
                    Snackbar.LENGTH_INDEFINITE
            ).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            startObservingSliceLiveData()
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun startObservingSliceLiveData() {
        TransitionManager.beginDelayedTransition(root)
        needPermissionGroup.visibility = View.INVISIBLE
        sliceView.visibility = View.VISIBLE

        val slice = sliceManager.bindSlice(sliceUri)
        sliceView.setSlice(slice)

        // TODO due to a bug in 28.0.0-alpha1, we can't use the LiveData version 😭
//        SliceLiveData.fromUri(this, sliceUri)
//            .observe(this, Observer({ sliceResult ->
//                sliceView.setSlice(sliceResult)
//                invalidateOptionsMenu()
//            }))
    }

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 1234
    }
}

