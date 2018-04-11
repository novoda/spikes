package com.novoda.spikes.arcore.rendering

import android.app.Activity
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import com.google.ar.core.Session
import com.novoda.spikes.arcore.DebugViewDisplayer
import com.novoda.spikes.arcore.google.helper.TapHelper
import com.novoda.spikes.arcore.google.rendering.BackgroundRenderer
import com.novoda.spikes.arcore.visualiser.ModelVisualiser
import com.novoda.spikes.arcore.visualiser.PlanesVisualiser
import com.novoda.spikes.arcore.visualiser.TrackedPointsVisualiser
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10


class NovodaSurfaceViewRenderer(private val context: Activity,
                                private val debugViewDisplayer: DebugViewDisplayer,
                                tapHelper: TapHelper) : GLSurfaceView.Renderer {

    private val backgroundRenderer = BackgroundRenderer()
    private val pointsVisualiser = TrackedPointsVisualiser(context)
    private val planesVisualiser = PlanesVisualiser(context)
    private val modelsVisualiser = ModelVisualiser(context, tapHelper, debugViewDisplayer)
    private val arCoreDataModel = ARCoreDataModel(context)


    override fun onDrawFrame(gl: GL10?) {
        if (!arCoreDataModel.isSessionReady()) {
            return
        }
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

        arCoreDataModel.update(backgroundRenderer.textureId)
        backgroundRenderer.draw(arCoreDataModel.frame)

        pointsVisualiser.drawTrackedPoints(arCoreDataModel)
        planesVisualiser.drawPlanes(arCoreDataModel)
        modelsVisualiser.drawModels(arCoreDataModel)

        debugViewDisplayer.display()
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        arCoreDataModel.onViewportChanged(width, height)
        GLES20.glViewport(0, 0, width, height)
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES20.glClearColor(0.1f, 0.1f, 0.1f, 1.0f)
        backgroundRenderer.createOnGlThread(context)
        pointsVisualiser.init()
        planesVisualiser.init()
        modelsVisualiser.init()
    }

    fun setSession(session: Session) {
        arCoreDataModel.session = session
    }

}
