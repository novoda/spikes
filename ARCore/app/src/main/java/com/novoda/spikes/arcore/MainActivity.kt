package com.novoda.spikes.arcore

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.google.ar.core.ArCoreApk
import com.google.ar.core.ArCoreApk.InstallStatus.INSTALL_REQUESTED
import com.google.ar.core.exceptions.*

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"
    private var shouldRequestARCoreInstall = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onResume() {
        super.onResume()
        checkARCoreIsInstalled()
    }

    private fun checkARCoreIsInstalled() {
        try {
            val requestInstall = ArCoreApk.getInstance().requestInstall(this, shouldRequestARCoreInstall)
            // We only care about 'INSTALL_REQUESTED'
            // Nothing to do if the returned value is 'INSTALLED'
            if (requestInstall == INSTALL_REQUESTED) {
                shouldRequestARCoreInstall = false
            }

        } catch (exception: Exception) {
            Log.e(TAG, "Exception creating session", exception)
            Toast.makeText(this, messageFromExceptionType(exception), Toast.LENGTH_SHORT).show()
            return
        }
    }

    private fun messageFromExceptionType(exception: Exception): String {
        return when (exception) {
            is UnavailableUserDeclinedInstallationException -> "Please install ARCore"
            is UnavailableArcoreNotInstalledException -> "Please install ARCore"
            is UnavailableApkTooOldException -> "Please update ARCore"
            is UnavailableSdkTooOldException -> "Please update this app"
            is UnavailableDeviceNotCompatibleException -> "This device does not support AR"
            else -> "Failed to create AR session"
        }
    }
}
