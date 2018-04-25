package com.novoda.spikes.arcore.rendering

import android.content.Context
import android.util.Log
import android.view.MotionEvent
import com.google.ar.core.*
import com.google.ar.core.exceptions.CameraNotAvailableException
import com.novoda.spikes.arcore.DebugViewDisplayer
import com.novoda.spikes.arcore.google.helper.DisplayRotationHelper
import com.novoda.spikes.arcore.google.helper.TapHelper
import com.novoda.spikes.arcore.google.rendering.PlaneRenderer
import com.novoda.spikes.arcore.rajawali.CameraBackground
import com.novoda.spikes.arcore.visualiser.ModelVisualiserRajawali
import org.rajawali3d.materials.textures.StreamingTexture
import org.rajawali3d.math.Matrix4
import org.rajawali3d.renderer.Renderer
import javax.microedition.khronos.opengles.GL10


class NovodaARCoreRajawaliRenderer(context: Context,
                                   tapHelper: TapHelper,
                                   private val debugViewDisplayer: DebugViewDisplayer,
                                   private val session: Session) : Renderer(context) {


    private val planeRenderer = PlaneRenderer()

    private lateinit var backgroundTexture: StreamingTexture
    private val displayRotationHelper = DisplayRotationHelper(context)
    private val modelVisualiserRajawali = ModelVisualiserRajawali(this, context, currentScene, textureManager, tapHelper)

    private val arCoreDataModelRajawali = ARCoreDataModelRajawali(context)

    override fun onResume() {
        super.onResume()

        displayRotationHelper.onResume()

        try {
            session.resume()
        } catch (e: CameraNotAvailableException) {
            // In some cases (such as another camera app launching) the camera may be given to
            // a different app instead. Handle this properly by showing a message and recreate the
            // session at the next iteration.
            Log.e("ARCoreRenderer", "Error", e)
        }
    }

    override fun onPause() {
        super.onPause()

        displayRotationHelper.onPause()

        session.pause()
    }

    override fun onRenderSurfaceSizeChanged(gl: GL10?, width: Int, height: Int) {
        super.onRenderSurfaceSizeChanged(gl, width, height)
        arCoreDataModelRajawali.onSurfaceChanged(width, height)
    }


    override fun initScene() {
        planeRenderer.createOnGlThread(context, "models/trigrid.png")

        // Create camera background
        val cameraBackground = CameraBackground()

        // Keep texture to field for later update
        backgroundTexture = cameraBackground.texture

        // Add to scene
        currentScene.addChild(cameraBackground)

        //  Spawn droid object in front of you


        modelVisualiserRajawali.init()
    }

    override fun onRender(ellapsedRealtime: Long, deltaTime: Double) {
        super.onRender(ellapsedRealtime, deltaTime)
        arCoreDataModelRajawali.update(session, currentScene, backgroundTexture.textureId)
        // If not tracking, don't draw 3d objects.
        if (arCoreDataModelRajawali.camera.trackingState == TrackingState.PAUSED) {
            return
        }

        planeRenderer.drawPlanes(
                session.getAllTrackables(Plane::class.java),
                arCoreDataModelRajawali.camera.displayOrientedPose,
                arCoreDataModelRajawali.cameraProjectionMatrix
        )

        modelVisualiserRajawali.drawModels(arCoreDataModelRajawali.frame)
        debugViewDisplayer.display()
    }

    override fun onOffsetsChanged(xOffset: Float, yOffset: Float, xOffsetStep: Float, yOffsetStep: Float, xPixelOffset: Int, yPixelOffset: Int) {}
    override fun onTouchEvent(event: MotionEvent?) {
        modelVisualiserRajawali.onTouchEvent(event)
    }
}
