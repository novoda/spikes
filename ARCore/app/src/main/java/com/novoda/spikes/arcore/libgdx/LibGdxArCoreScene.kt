package com.novoda.spikes.arcore.libgdx

import android.util.Log
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.math.Quaternion
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.Array
import com.google.ar.core.Anchor
import com.google.ar.core.Frame
import com.google.ar.core.Plane
import com.google.ar.core.TrackingState
import com.novoda.spikes.arcore.libgdx.gdx.ARCoreScene
import com.novoda.spikes.arcore.libgdx.gdx.PlaneAttachment
import java.util.HashMap

class LibGdxArCoreScene : ARCoreScene() {

    private val assetManager = AssetManager()
    private lateinit var andyModel: AndyModel

    // Keep the objects in the scene mapped by the anchor id.
    private val instances = HashMap<Anchor, PlaneAttachment<ModelInstance>>()

    override fun create() {
        super.create()
        andyModel = AndyModel(assetManager)
    }

    override fun render(frame: Frame, modelBatch: ModelBatch) {
        assetManager.update()
        if (!andyModel.isInitialized) {
            andyModel.initialize(assetManager)
        }
        drawPlanes(modelBatch)

        // Handle taps to create androids.
        handleInput(frame)

        for (anchor in instances.keys) {
            instances.getOrElse(anchor, { throw Exception("Anchor not present: $anchor") }).apply {
                val pos = Vector3(pose.tx(), pose.ty(), pose.tz())
                val rot = Quaternion(pose.qx(), pose.qy(), pose.qz(), pose.qw())
                data.transform.set(pos, rot)
            }
        }

//        val models = instances.values.map { it.data } // This is quite slow!
        val models = mutableListOf<ModelInstance>()
        for (p in instances.values) {
            models.add(p.data)
        }
        modelBatch.render<ModelInstance>(models)
    }

    private fun drawPlanes(modelBatch: ModelBatch) {
        val planeInstances = Array<ModelInstance>()
        var index = 0
        for (plane in session.getAllTrackables(Plane::class.java)) {

            // check for planes that are no longer valid
            if (plane.subsumedBy != null || plane.trackingState == TrackingState.STOPPED) {
                continue
            }
            // New plane
            val instance = ModelInstance(PlaneModel.createPlane(plane, index++))
            instance.transform.setToTranslation(
                    plane.centerPose.tx(), plane.centerPose.ty(), plane.centerPose.tz())
            planeInstances.add(instance)
        }
        modelBatch.render(planeInstances)
    }

    private fun handleInput(frame: Frame) {
        if (Gdx.input.justTouched()) {

            val x = Gdx.input.x
            val y = Gdx.input.y
            for (hit in frame.hitTest(x.toFloat(), y.toFloat())) {
                // Check if any plane was hit, and if it was hit inside the plane polygon.
                val trackable = hit.trackable
                if (trackable is Plane && trackable.isPoseInPolygon(hit.hitPose)) {
                    // Cap the number of objects created. This avoids overloading both the
                    // rendering system and ARCore.
                    if (instances.size >= 20) {
                        val key = instances.keys.first()
                        instances.remove(key)
                        key.detach()
                    }
                    // Adding an Anchor tells ARCore that it should track this position in
                    // space. This anchor will be used in PlaneAttachment to place the 3d model
                    // in the correct position relative both to the world and to the plane.
                    val item = andyModel.createInstance()
                    val planeAttachment = PlaneAttachment(
                            hit.trackable as Plane,
                            hit.createAnchor(),
                            item)

                    instances.put(planeAttachment.anchor, planeAttachment)

                    val p = planeAttachment.pose
                    // position and rotate
                    val dir = Quaternion(p.qx(), p.qy(), p.qz(), p.qw())
                    val pos = Vector3(p.tx(), p.ty(), p.tz())
                    item.transform.translate(pos)
                    item.transform.set(dir)
                    Log.w("TODO", "Anchor created: %.2f, %.2f, %.2f".format(planeAttachment.anchor.pose.xAxis[0], planeAttachment.anchor.pose.yAxis[0], planeAttachment.anchor.pose.zAxis[0]))
                    break
                }
            }
        }
    }

}
