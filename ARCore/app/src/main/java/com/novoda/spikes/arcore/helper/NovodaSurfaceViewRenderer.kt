package com.novoda.spikes.arcore.helper

import android.app.Activity
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.view.Display
import android.view.WindowManager
import com.google.ar.core.Session
import com.novoda.spikes.arcore.rendering.BackgroundRenderer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class NovodaSurfaceViewRenderer(val context: Activity) : GLSurfaceView.Renderer {

    private var display: Display = context.getSystemService(WindowManager::class.java).defaultDisplay
    private var viewportChanged: Boolean = true
    private var viewportWidth: Int = 0
    private var viewportHeight: Int = 0

    private val backgroundRenderer = BackgroundRenderer()
    lateinit var session: Session

    override fun onDrawFrame(gl: GL10?) {
        if (this::session.isInitialized.not()) {
            return
        }
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
        updateSessionIfNeeded(session)
        session.setCameraTextureName(backgroundRenderer.textureId)
        val frame = session.update()
        backgroundRenderer.draw(frame)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        viewportWidth = width
        viewportHeight = height
        GLES20.glViewport(0, 0, width, height)
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES20.glClearColor(0.1f, 0.1f, 0.1f, 1.0f)
        backgroundRenderer.createOnGlThread(context)
    }

    private fun updateSessionIfNeeded(session: Session) {
        if (viewportChanged) {
            val displayRotation = display.getRotation()
            session.setDisplayGeometry(displayRotation, viewportWidth, viewportHeight)
            viewportChanged = false
        }
    }

}