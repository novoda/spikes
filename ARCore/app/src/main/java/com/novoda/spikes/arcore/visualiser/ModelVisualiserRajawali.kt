package com.novoda.spikes.arcore.visualiser

import android.content.Context
import android.graphics.Color
import android.view.MotionEvent
import com.google.ar.core.Frame
import com.google.ar.core.Plane
import com.google.ar.core.Point
import com.google.ar.core.TrackingState
import com.novoda.spikes.arcore.R
import com.novoda.spikes.arcore.google.helper.TapHelper
import org.rajawali3d.Object3D
import org.rajawali3d.lights.DirectionalLight
import org.rajawali3d.loader.LoaderOBJ
import org.rajawali3d.materials.Material
import org.rajawali3d.materials.textures.Texture
import org.rajawali3d.materials.textures.TextureManager
import org.rajawali3d.math.vector.Vector3
import org.rajawali3d.renderer.Renderer
import org.rajawali3d.scene.Scene
import org.rajawali3d.util.ObjectColorPicker
import org.rajawali3d.util.OnObjectPickedListener

class ModelVisualiserRajawali(
        private val renderer: Renderer,
        private val context: Context,
        private val scene: Scene,
        private val textureManager: TextureManager,
        private val tapHelper: TapHelper) : OnObjectPickedListener {
    private val mDirectionalLight = DirectionalLight(1.0, .2, -1.0).apply {
        setColor(1.0f, 1.0f, 1.0f)
        power = 2f
    }


    val mPicker = ObjectColorPicker(renderer).apply {
        setOnObjectPickedListener(this@ModelVisualiserRajawali);
    }

    private lateinit var droid: Object3D

    fun init() {
//        val objParser = LoaderOBJ(context.resources, textureManager, R.raw.trophy_obj) // TEXTURAS
////        val objParser = LoaderOBJ(this, R.raw.andy)
//        objParser.parse()
//
//
//        droid = objParser.parsedObject
////        droid = objParser.parsedObject.getChildAt(0) // TEXTURAS
//        droid.setPosition(0.0, 0.0, -1.0)
//        droid.material = Material().apply {
//            addTexture(Texture("droid", R.raw.andy_texture))
//            color = Color.BLACK
//
//            diffuseMethod = DiffuseMethod.Lambert()
//            enableLighting(true)
//            colorInfluence = 0f
//        }
//
////        currentScene.addLight(mDirectionalLight)

        //  Spawn droid object in front of you
        val objParser = LoaderOBJ(renderer, R.raw.andy)
        objParser.parse()


        droid = objParser.parsedObject.getChildAt(1) // TEXTURAS
        droid.setPosition(0.0, 0.0, -1.0)
        droid.material = Material().apply {
            addTexture(Texture("droid", R.raw.andy_texture))
            color = Color.BLACK

        }

        scene.addChild(droid)
    }

    fun drawModels(frame: Frame) {
        val tap = tapHelper.poll()

        if (tap != null && frame.camera.trackingState == TrackingState.TRACKING) {
            onTap(frame, tap)
        }
    }

    private fun onTap(frame: Frame, tap: MotionEvent) {
        for (hit in frame.hitTest(tap)) {

//            debugViewDisplayer.append("Hit")
            // Check if any plane was hit, and if it was hit inside the plane polygon
            val trackable = hit.trackable


            // Creates an anchor if a plane or an oriented point was hit.
            if (trackable is Plane && trackable.isPoseInPolygon(hit.hitPose) || trackable is Point && trackable.orientationMode == Point.OrientationMode.ESTIMATED_SURFACE_NORMAL) {

//                debugViewDisplayer.append("\t... on plane")


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
                scene.addChild(newDroid)

                mPicker.registerObject(newDroid)

                break
            }
        }
    }

    override fun onNoObjectPicked() {
        // no-op
    }

    override fun onObjectPicked(model: Object3D) {
        model.rotate(Vector3.Axis.Y, 4.0)
    }

    fun onTouchEvent(event: MotionEvent?) {
        if (event?.action == MotionEvent.ACTION_DOWN) {
            mPicker.getObjectAt(event.x, event.y)
        }
    }
}
