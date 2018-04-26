package com.novoda.spikes.arcore.rendering

import android.content.Context
import android.view.MotionEvent
import com.google.ar.core.Session
import com.novoda.spikes.arcore.DebugViewDisplayer
import com.novoda.spikes.arcore.google.helper.TapHelper
import com.novoda.spikes.arcore.rajawali.CameraBackground
import com.novoda.spikes.arcore.visualiser.ModelVisualiserRajawali
import com.novoda.spikes.arcore.visualiser.PlanesVisualiser
import com.novoda.spikes.arcore.visualiser.TrackedPointsVisualiser
import org.rajawali3d.materials.textures.StreamingTexture
import org.rajawali3d.renderer.Renderer
import javax.microedition.khronos.opengles.GL10


class NovodaARCoreRajawaliRenderer(context: Context,
                                   tapHelper: TapHelper,
                                   private val debugViewDisplayer: DebugViewDisplayer,
                                   session: Session) : Renderer(context) {


    private val pointsVisualiser = TrackedPointsVisualiser(context)

    private lateinit var backgroundTexture: StreamingTexture
    private val modelVisualiserRajawali = ModelVisualiserRajawali(this, context, textureManager, tapHelper)
    private val arCoreDataModelRajawali = ARCoreDataModelRajawali(currentScene, context, session)
    private val planesVisualiser = PlanesVisualiser(context)

    override fun initScene() {
        CameraBackground().apply {
            backgroundTexture = texture
            currentScene.addChild(this)
        }
        modelVisualiserRajawali.init()
        pointsVisualiser.init()
        planesVisualiser.init()
    }


    override fun onRender(ellapsedRealtime: Long, deltaTime: Double) {
        super.onRender(ellapsedRealtime, deltaTime)
        arCoreDataModelRajawali.update(backgroundTexture.textureId)
        // If not tracking, don't draw 3d objects.
        if (!arCoreDataModelRajawali.isSessionReady()) {
            return
        }

        // A background renderer is not needed as a CameraBackground is already added to the scene

        pointsVisualiser.drawTrackedPoints(arCoreDataModelRajawali)
        planesVisualiser.drawPlanes(arCoreDataModelRajawali)
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
