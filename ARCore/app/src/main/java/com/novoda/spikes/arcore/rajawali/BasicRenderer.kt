package com.novoda.spikes.arcore.rajawali

import android.content.Context
import android.util.Log
import android.view.MotionEvent
import com.novoda.spikes.arcore.R
import org.rajawali3d.lights.DirectionalLight
import org.rajawali3d.materials.Material
import org.rajawali3d.materials.methods.DiffuseMethod
import org.rajawali3d.materials.textures.ATexture
import org.rajawali3d.materials.textures.Texture
import org.rajawali3d.math.vector.Vector3
import org.rajawali3d.primitives.Sphere
import org.rajawali3d.renderer.Renderer


class BasicRenderer(context: Context) : Renderer(context) {
    private val mDirectionalLight = DirectionalLight(1.0, .2, -1.0).apply {
        setColor(1.0f, 1.0f, 1.0f)
        power = 2f
    }
    private val earthSphere = Sphere(1f, 24, 24)

    init {
        frameRate = 60.toDouble()
    }

    override fun initScene() {
        currentScene.addLight(mDirectionalLight)

        val material = Material()
        material.enableLighting(true)
        material.diffuseMethod = DiffuseMethod.Lambert()
        material.colorInfluence = 0f
        val earthTexture = Texture("Earth", R.drawable.earthtruecolor_nasa_big)
        try {
            material.addTexture(earthTexture)

        } catch (error: ATexture.TextureException) {
            Log.d("BasicRenderer" + ".initScene", error.toString())
        }


        earthSphere.setMaterial(material)
        currentScene.addChild(earthSphere)
        currentCamera.z = 4.2

    }

    override fun onRender(ellapsedRealtime: Long, deltaTime: Double) {
        super.onRender(ellapsedRealtime, deltaTime)
        earthSphere.rotate(Vector3.Axis.Y, 4.0)
    }

    override fun onTouchEvent(event: MotionEvent) {}

    override fun onOffsetsChanged(x: Float, y: Float, z: Float, w: Float, i: Int, j: Int) {}
}