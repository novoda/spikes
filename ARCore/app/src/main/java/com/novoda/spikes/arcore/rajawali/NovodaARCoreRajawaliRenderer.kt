package com.novoda.spikes.arcore.rajawali

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.MotionEvent
import com.google.ar.core.*
import com.google.ar.core.exceptions.CameraNotAvailableException
import com.novoda.spikes.arcore.DebugViewDisplayer
import com.novoda.spikes.arcore.R
import com.novoda.spikes.arcore.google.helper.DisplayRotationHelper
import com.novoda.spikes.arcore.google.helper.TapHelper
import com.novoda.spikes.arcore.google.rendering.PlaneRenderer
import org.rajawali3d.Object3D
import org.rajawali3d.loader.LoaderOBJ
import org.rajawali3d.materials.Material
import org.rajawali3d.materials.textures.StreamingTexture
import org.rajawali3d.materials.textures.Texture
import org.rajawali3d.math.Matrix4
import org.rajawali3d.math.vector.Vector3
import org.rajawali3d.renderer.Renderer
import org.rajawali3d.util.ObjectColorPicker
import org.rajawali3d.util.OnObjectPickedListener
import javax.microedition.khronos.opengles.GL10


open class NovodaARCoreRajawaliRenderer(context: Context,
                                        private val tapHelper: TapHelper,
                                        private val debugViewDisplayer: DebugViewDisplayer,
                                        private val session: Session) : Renderer(context), OnObjectPickedListener {


    private val planeRenderer = PlaneRenderer()

    private lateinit var droid: Object3D
    private lateinit var backgroundTexture: StreamingTexture
    private val displayRotationHelper = DisplayRotationHelper(context)

    // Temporary matrix values
    private val projectionMatrixValues = FloatArray(16)
    private val viewMatrixValues = FloatArray(16)
    private val projectionMatrix = Matrix4()
    private val viewMatrix = Matrix4()
    private val allPlanes = ArrayList<Plane>()

    val mPicker = ObjectColorPicker(this).apply {
        setOnObjectPickedListener(this@NovodaARCoreRajawaliRenderer);
    }


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
        displayRotationHelper.onSurfaceChanged(width, height)
    }


    override fun initScene() {

        // Create camera background
        val cameraBackground = CameraBackground()

        // Keep texture to field for later update
        backgroundTexture = cameraBackground.texture

        // Add to scene
        currentScene.addChild(cameraBackground)

        //  Spawn droid object in front of you
        val objParser = LoaderOBJ(this, R.raw.andy)
        objParser.parse()
        droid = objParser.parsedObject.getChildAt(1)
        droid.setPosition(0.0, 0.0, -1.0)
        droid.material = Material().apply {
            addTexture(Texture("droid", R.raw.andy_texture))
            color = Color.BLACK
        }
        currentScene.addChild(droid)


        planeRenderer.createOnGlThread(context, "models/trigrid.png")


    }

    override fun onRender(ellapsedRealtime: Long, deltaTime: Double) {
        super.onRender(ellapsedRealtime, deltaTime)

        // Notify ARCore session that the view size changed so that the perspective matrix and
        // the video background can be properly adjusted.
        displayRotationHelper.updateSessionIfNeeded(session)

        session.setCameraTextureName(backgroundTexture.textureId)

        val frame = session.update()
        val camera = frame.camera

        // If not tracking, don't draw 3d objects.
        if (camera.trackingState == TrackingState.PAUSED) {
            return
        }

        val tap = tapHelper.poll()

        if (tap != null && frame.camera.trackingState == TrackingState.TRACKING) {
            onTap(frame, tap)
        }


        val cameraProjectionMatrix = FloatArray(16)
        camera.getProjectionMatrix(cameraProjectionMatrix, 0, 0.1f, 100.0f)

        planeRenderer.drawPlanes(
                session.getAllTrackables(Plane::class.java),
                camera.displayOrientedPose,
                cameraProjectionMatrix
        )


        // Update projection matrix.
        updateCameraView(camera)


        debugViewDisplayer.display()
    }


    private fun onTap(frame: Frame, tap: MotionEvent) {
        for (hit in frame.hitTest(tap)) {

            debugViewDisplayer.append("Hit")
            // Check if any plane was hit, and if it was hit inside the plane polygon
            val trackable = hit.trackable


            // Creates an anchor if a plane or an oriented point was hit.
            if (trackable is Plane && trackable.isPoseInPolygon(hit.hitPose) || trackable is Point && trackable.orientationMode == Point.OrientationMode.ESTIMATED_SURFACE_NORMAL) {

                debugViewDisplayer.append("\t... on plane")


                // Create anchor at touched place
                val anchor = hit.createAnchor()
//                val rot = FloatArray(4)
//                anchor.pose.getRotationQuaternion(rot, 0)
                val translation = FloatArray(3)
                anchor.pose.getTranslation(translation, 0)


                // Spawn new droid object at anchor position
                val newDroid = droid.clone().apply {
                    setPosition(translation[0].toDouble(), translation[1].toDouble(), translation[2].toDouble())
                }
                currentScene.addChild(newDroid)
                mPicker.registerObject(newDroid)

                break
            }
        }
    }

    private fun updateCameraView(camera: Camera) {
        camera.getProjectionMatrix(projectionMatrixValues, 0, 0.1f, 100.0f)
        projectionMatrix.setAll(projectionMatrixValues)

        currentScene.camera.projectionMatrix = projectionMatrix

        // Update camera matrix.
        camera.getViewMatrix(viewMatrixValues, 0)
        viewMatrix.setAll(viewMatrixValues).inverse()

        currentScene.camera.setRotation(viewMatrix)
        currentScene.camera.position = viewMatrix.translation
    }

    override fun onOffsetsChanged(xOffset: Float, yOffset: Float, xOffsetStep: Float, yOffsetStep: Float, xPixelOffset: Int, yPixelOffset: Int) {}
    override fun onTouchEvent(event: MotionEvent?) {

        if (event?.action == MotionEvent.ACTION_DOWN) {
            mPicker.getObjectAt(event.x, event.y)
        }


    }

    override fun onNoObjectPicked() {


    }

    override fun onObjectPicked(model: Object3D) {

        model.rotate(Vector3.Axis.Y, 4.0)
    }
}
