package com.novoda.spikes.arcore.rendering

import android.content.Context
import com.google.ar.core.Session
import org.rajawali3d.math.Matrix4
import org.rajawali3d.scene.Scene

class ARCoreDataModelRajawali(context: Context, private val currentScene: Scene, session: Session) : BaseARCoreDataModel(context) {
    init {
        this.session = session
    }

    // Temporary matrix values
    private val projectionMatrix = Matrix4()
    private val viewMatrix = Matrix4()

    override fun onCameraUpdated() {
        projectionMatrix.setAll(cameraProjectionMatrix)
        currentScene.camera.projectionMatrix = projectionMatrix


        viewMatrix.setAll(cameraViewMatrix).inverse()
        currentScene.camera.setRotation(viewMatrix)
        currentScene.camera.position = viewMatrix.translation
    }


}