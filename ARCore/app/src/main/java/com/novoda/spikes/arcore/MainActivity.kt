package com.novoda.spikes.arcore

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.google.ar.core.ArCoreApk
import com.google.ar.core.ArCoreApk.InstallStatus.INSTALL_REQUESTED
import com.google.ar.core.Session
import com.google.ar.core.exceptions.*

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"
    private var shouldRequestARCoreInstall = true
    private lateinit var arSession: Session

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
            createOrResumeARSession()
        }
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
            showMessage(messageFromExceptionType(exception))
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

    // ARCore requires camera permissions to operate.
    private fun isCameraPermissionGranted(): Boolean {
        if (!CameraPermissionHelper.hasCameraPermission(this)) {
            CameraPermissionHelper.requestCameraPermission(this)
            return false
        }
        return true
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

    private fun createOrResumeARSession() {
        if (this::arSession.isInitialized.not()) {
            arSession = Session(this)
        }
        try {
            arSession.resume()
        } catch (e: CameraNotAvailableException) {
            // In some cases (such as another camera app launching) the camera may be given to
            // a different app instead. Handle this properly by showing a message and recreate the
            // session at the next iteration.
            showMessage("Camera not available. Please restart the app.")
            return
        }
    }

    public override fun onPause() {
        super.onPause()
        if (this::arSession.isInitialized) {
            arSession.pause()
        }
    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
