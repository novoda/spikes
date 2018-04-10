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
        checkAREnvironment()
    }

    private fun checkAREnvironment() {
        if (isARCoreIsInstalled() && isCameraPermissionGranted()) {
            Toast.makeText(this, "All good, can do AR stuff now", Toast.LENGTH_SHORT).show()
        }
    }

    // ARCore requires camera permissions to operate.
    private fun isCameraPermissionGranted(): Boolean {
        if (!CameraPermissionHelper.hasCameraPermission(this)) {
            CameraPermissionHelper.requestCameraPermission(this)
            return false
        }
        return true
    }

    private fun isARCoreIsInstalled(): Boolean {
        try {
            val requestInstall = ArCoreApk.getInstance().requestInstall(this, shouldRequestARCoreInstall)
            // We only care about 'INSTALL_REQUESTED'
            // Nothing to do if the returned value is 'INSTALLED'
            if (requestInstall == INSTALL_REQUESTED) {
                shouldRequestARCoreInstall = false
                return false
            }

        } catch (exception: Exception) {
            Log.e(TAG, "Exception creating session", exception)
            Toast.makeText(this, messageFromExceptionType(exception), Toast.LENGTH_SHORT).show()
            return false
        }
        return true
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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, results: IntArray) {
        if (!CameraPermissionHelper.hasCameraPermission(this)) {
            Toast.makeText(this, "Camera permission is needed to run this application", Toast.LENGTH_LONG).show()
            if (!CameraPermissionHelper.shouldShowRequestPermissionRationale(this)) {
                // Permission denied with checking "Do not ask again".
                CameraPermissionHelper.launchPermissionSettings(this)
            }
            finish()
        }
    }
}
