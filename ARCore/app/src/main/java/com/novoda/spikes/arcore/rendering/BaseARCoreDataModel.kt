package com.novoda.spikes.arcore.rendering

import android.content.Context
import android.view.Display
import android.view.WindowManager
import com.google.ar.core.Camera
import com.google.ar.core.Frame
import com.google.ar.core.Session

abstract class BaseARCoreDataModel(context: Context) {

    private val display: Display = context.getSystemService(WindowManager::class.java).defaultDisplay
    private var viewportChanged: Boolean = true
    private var viewportWidth: Int = 0
    private var viewportHeight: Int = 0
    val cameraViewMatrix = FloatArray(16)
    val cameraProjectionMatrix = FloatArray(16)
    lateinit var session: Session
    lateinit var frame: Frame
    lateinit var camera: Camera

    abstract fun isSessionReady(): Boolean

    fun update(textureId: Int) {
        updateSessionIfNeeded(session)
        // Sets the OpenGL texture name (id) that will allow GPU access to the camera image.
        session.setCameraTextureName(textureId)
        frame = session.update()

        camera = frame.camera
        camera.getProjectionMatrix(cameraProjectionMatrix, 0, 0.1f, 100.0f)
        camera.getViewMatrix(cameraViewMatrix, 0)

        onCameraUpdated()
    }

    private fun updateSessionIfNeeded(session: Session) {
        if (viewportChanged) {
            session.setDisplayGeometry(display.rotation, viewportWidth, viewportHeight)
            viewportChanged = false
        }
    }

    fun onViewportChanged(width: Int, height: Int) {
        viewportWidth = width
        viewportHeight = height
        viewportChanged = true
    }

    abstract fun onCameraUpdated()
}