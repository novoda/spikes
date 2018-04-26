package com.novoda.spikes.arcore.rendering

import android.content.Context
import android.view.MotionEvent
import com.google.ar.core.Plane
import com.google.ar.core.Session
import com.google.ar.core.TrackingState
import com.novoda.spikes.arcore.DebugViewDisplayer
import com.novoda.spikes.arcore.google.helper.TapHelper
import com.novoda.spikes.arcore.google.rendering.PlaneRenderer
import com.novoda.spikes.arcore.rajawali.CameraBackground
import com.novoda.spikes.arcore.visualiser.ModelVisualiserRajawali
import com.novoda.spikes.arcore.visualiser.TrackedPointsVisualiser
import org.rajawali3d.materials.textures.StreamingTexture
import org.rajawali3d.renderer.Renderer
import javax.microedition.khronos.opengles.GL10


class NovodaARCoreRajawaliRenderer(context: Context,
                                   tapHelper: TapHelper,
                                   private val debugViewDisplayer: DebugViewDisplayer,
                                   private val session: Session) : Renderer(context) {


    private val pointsVisualiser = TrackedPointsVisualiser(context)
    private val planeRenderer = PlaneRenderer()

    private lateinit var backgroundTexture: StreamingTexture
    private val modelVisualiserRajawali = ModelVisualiserRajawali(this, context, textureManager, tapHelper)
    private val arCoreDataModelRajawali = ARCoreDataModelRajawali(context, currentScene, session)


    override fun initScene() {
        planeRenderer.createOnGlThread(context, "models/trigrid.png")
        CameraBackground().apply {
            backgroundTexture = texture
            currentScene.addChild(this)
        }
        modelVisualiserRajawali.init()
        pointsVisualiser.init()
    }


    override fun onRender(ellapsedRealtime: Long, deltaTime: Double) {
        super.onRender(ellapsedRealtime, deltaTime)
        arCoreDataModelRajawali.update(backgroundTexture.textureId)
        // If not tracking, don't draw 3d objects.
        if (arCoreDataModelRajawali.camera.trackingState == TrackingState.PAUSED) {
            return
        }

        //pointsVisualiser.drawTrackedPoints(arCoreDataModelRajawali)
        planeRenderer.drawPlanes(
                session.getAllTrackables(Plane::class.java),
                arCoreDataModelRajawali.camera.displayOrientedPose,
                arCoreDataModelRajawali.cameraProjectionMatrix
        )

        modelVisualiserRajawali.drawModels(arCoreDataModelRajawali.frame)
        debugViewDisplayer.display()
    }

    override fun onRenderSurfaceSizeChanged(gl: GL10?, width: Int, height: Int) {
        super.onRenderSurfaceSizeChanged(gl, width, height)
        arCoreDataModelRajawali.onViewportChanged(width, height)
    }

    override fun onOffsetsChanged(xOffset: Float, yOffset: Float, xOffsetStep: Float, yOffsetStep: Float, xPixelOffset: Int, yPixelOffset: Int) {}
    override fun onTouchEvent(event: MotionEvent?) {
        modelVisualiserRajawali.onTouchEvent(event)
    }

}
