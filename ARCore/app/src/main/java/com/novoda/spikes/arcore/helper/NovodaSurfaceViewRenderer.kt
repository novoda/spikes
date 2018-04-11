package com.novoda.spikes.arcore.helper

import android.app.Activity
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.view.Display
import android.view.WindowManager
import com.google.ar.core.*
import com.novoda.spikes.arcore.DebugViewDisplayer
import com.novoda.spikes.arcore.rendering.BackgroundRenderer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class NovodaSurfaceViewRenderer(private val context: Activity,
                                private val debugViewDisplayer: DebugViewDisplayer,
                                private val tapHelper: TapHelper) : GLSurfaceView.Renderer {

    private val TAG: String = "NovodaSurfaceViewRenderer"
    private val display: Display = context.getSystemService(WindowManager::class.java).defaultDisplay
    private val backgroundRenderer = BackgroundRenderer()
    private val pointsVisualiser = TrackedPointsVisualiser(context)
    private val planesVisualiser = PlanesVisualiser(context)
    private val anchorsVisualiser = AnchorsVisualiser(context)

    private val cameraViewMatrix = FloatArray(16)
    private val cameraProjectionMatrix = FloatArray(16)

    private var viewportChanged: Boolean = true
    private var viewportWidth: Int = 0
    private var viewportHeight: Int = 0

    lateinit var session: Session

    override fun onDrawFrame(gl: GL10?) {
        if (this::session.isInitialized.not()) {
            return
        }
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
        updateSessionIfNeeded(session)

        // Sets the OpenGL texture name (id) that will allow GPU access to the camera image.
        session.setCameraTextureName(backgroundRenderer.textureId)
        // Updates the state of the ARCore system.
        val frame = session.update()

        // Draw the background (in this case, the video stream input from the camera)
        backgroundRenderer.draw(frame)


        val camera = frame.camera


        camera.getProjectionMatrix(cameraProjectionMatrix, 0, 0.1f, 100.0f)
        camera.getViewMatrix(cameraViewMatrix, 0)

        pointsVisualiser.visualiseTrackedPoints(frame, cameraViewMatrix, cameraProjectionMatrix)
        planesVisualiser.visualisePlanes(session, camera, cameraProjectionMatrix)

        anchorsVisualiser.createTouchAnchors(camera, frame, tapHelper)
        anchorsVisualiser.visualiseTouchAnchors(frame, cameraViewMatrix, cameraProjectionMatrix)

        debugViewDisplayer.display()
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        viewportWidth = width
        viewportHeight = height
        GLES20.glViewport(0, 0, width, height)
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES20.glClearColor(0.1f, 0.1f, 0.1f, 1.0f)
        backgroundRenderer.createOnGlThread(context)
        pointsVisualiser.init()
        planesVisualiser.init()
        anchorsVisualiser.init()
    }

    private fun updateSessionIfNeeded(session: Session) {
        if (viewportChanged) {
            session.setDisplayGeometry(display.rotation, viewportWidth, viewportHeight)
            viewportChanged = false
        }
    }



}
