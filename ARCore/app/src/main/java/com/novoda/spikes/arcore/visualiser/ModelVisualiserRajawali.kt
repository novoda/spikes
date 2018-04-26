package com.novoda.spikes.arcore.visualiser

import android.content.Context
import android.graphics.Color
import android.view.MotionEvent
import com.google.ar.core.*
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
import org.rajawali3d.util.ObjectColorPicker
import org.rajawali3d.util.OnObjectPickedListener

class ModelVisualiserRajawali(
        private val renderer: Renderer,
        private val context: Context,
        private val textureManager: TextureManager,
        tapHelper: TapHelper) : BaseModelVisualiser(tapHelper), OnObjectPickedListener {


    private val mDirectionalLight = DirectionalLight(1.0, .2, -1.0).apply {
        setColor(1.0f, 1.0f, 1.0f)
        power = 2f
    }


    val mPicker = ObjectColorPicker(renderer).apply {
        setOnObjectPickedListener(this@ModelVisualiserRajawali);
    }


    private lateinit var droid: Object3D

    override fun init() {
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

        renderer.currentScene.addChild(droid)
    }

    override fun onAnchorCreated(anchor: Anchor) {

        val translation = FloatArray(3)
        anchor.pose.getTranslation(translation, 0)


        // Spawn new droid object at anchor position
        val newDroid = droid.clone().apply {
            setPosition(translation[0].toDouble(), translation[1].toDouble(), translation[2].toDouble())
        }
        renderer.currentScene.addChild(newDroid)

        mPicker.registerObject(newDroid)
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
