package com.novoda.spikes.arcore

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.google.ar.core.Session
import com.google.ar.core.exceptions.CameraNotAvailableException
import com.novoda.spikes.arcore.helper.ARCoreDependenciesHelper
import com.novoda.spikes.arcore.helper.CameraPermissionHelper

class MainActivity : AppCompatActivity() {
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
        ARCoreDependenciesHelper.isARCoreIsInstalled(this).apply {
            if (isARCoreInstalled && CameraPermissionHelper.isCameraPermissionGranted(this@MainActivity)) {
                createOrResumeARSession()
            } else {
                showMessage(message)
            }
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
