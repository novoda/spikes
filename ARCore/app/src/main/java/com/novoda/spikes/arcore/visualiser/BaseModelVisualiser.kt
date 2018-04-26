package com.novoda.spikes.arcore.visualiser

import com.google.ar.core.*
import com.novoda.spikes.arcore.google.helper.TapHelper
import java.util.*

abstract class BaseModelVisualiser(private val tapHelper: TapHelper) {
    private val anchors = ArrayList<Anchor>() // Anchors created from taps used for object placing.

    abstract fun init()

    fun drawModels(frame: Frame) {
        createAnchors(frame)
    }

    private fun createAnchors(frame: Frame) {
        tapHelper.poll()?.apply {
            if (frame.camera.trackingState == TrackingState.TRACKING) {
                for (hit in frame.hitTest(this)) {
                    val trackable = hit.trackable
                    if (shouldCreateAnchor(trackable, hit)) {

                        if (anchors.size >= 20) {
                            anchors.get(0).detach()
                            anchors.removeAt(0)
                        }
                        // Adding an Anchor tells ARCore that it should track this position in
                        // space. This anchor is created on the Plane to place the 3D model
                        // in the correct position relative both to the world and to the plane.
                        val anchor = hit.createAnchor()
                        anchors.add(anchor)
                        onAnchorCreated(anchor)
                        break
                    }
                }
            }
        }
    }

    private fun shouldCreateAnchor(trackable: Trackable?, hit: HitResult): Boolean {
        return when (trackable) {
            is Plane -> trackable.isPoseInPolygon(hit.hitPose)
            is Point -> trackable.orientationMode == Point.OrientationMode.ESTIMATED_SURFACE_NORMAL
            else -> false
        }
    }

    abstract fun onAnchorCreated(anchor: Anchor)
}