package com.novoda.spikes.arcore.rendering

import android.content.Context
import android.view.Display
import android.view.WindowManager
import com.google.ar.core.Camera
import com.google.ar.core.Frame
import com.google.ar.core.Session
import org.rajawali3d.math.Matrix4
import org.rajawali3d.scene.Scene

class ARCoreDataModelRajawali(context: Context) {
    private val display: Display = context.getSystemService(WindowManager::class.java).defaultDisplay
    private var viewportChanged: Boolean = true
    private var viewportWidth: Int = 0
    private var viewportHeight: Int = 0

    // Temporary matrix values
    private val projectionMatrix = Matrix4()
    private val viewMatrix = Matrix4()
    val cameraProjectionMatrix = FloatArray(16)
    val cameraViewMatrix = FloatArray(16)

    lateinit var frame: Frame
    lateinit var camera: Camera


    fun update(session: Session, currentScene: Scene, textureId: Int) {
        updateSessionIfNeeded(session)
        session.setCameraTextureName(textureId)
        frame = session.update()
        camera = frame.camera

        // Update projection matrix.
        camera.getProjectionMatrix(cameraProjectionMatrix, 0, 0.1f, 100.0f)
        projectionMatrix.setAll(cameraProjectionMatrix)
        currentScene.camera.projectionMatrix = projectionMatrix
        camera.getViewMatrix(cameraViewMatrix, 0)
        viewMatrix.setAll(cameraViewMatrix).inverse()
        currentScene.camera.setRotation(viewMatrix)
        currentScene.camera.position = viewMatrix.translation
    }

    private fun updateSessionIfNeeded(session: Session) {
        if (viewportChanged) {
            session.setDisplayGeometry(display.rotation, viewportWidth, viewportHeight)
            viewportChanged = false
        }
    }

    fun onSurfaceChanged(width: Int, height: Int) {
        viewportWidth = width
        viewportHeight = height
    }
}