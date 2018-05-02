package com.novoda.spikes.arcore.visualiser

import android.content.Context
import com.google.ar.core.*
import com.novoda.spikes.arcore.DebugViewDisplayer
import com.novoda.spikes.arcore.ModelCollection
import com.novoda.spikes.arcore.google.helper.TapHelper
import com.novoda.spikes.arcore.rendering.ARCoreDataModel
import com.novoda.spikes.arcore.rendering.PolyAssetRenderer


class ModelVisualiser(private val context: Context,
                      private val modelCollection: ModelCollection,
                      private val tapHelper: TapHelper,
                      private val debugViewDisplayer: DebugViewDisplayer) {

    private val anchors = HashMap<Anchor, PolyAssetRenderer>() // Anchors created from taps used for object placing.
    private val anchorMatrix = FloatArray(16) // Temporary matrix allocated here to reduce number of allocations for each frame.

    fun init() {
    }

    fun drawModels(model: ARCoreDataModel) {
        val currentModel = modelCollection.currentModel
        if (currentModel != null) {
            createTouchAnchors(model.camera, model.frame, currentModel)
        }
        addVirtualObjectModelToAnchor(model.frame, model.cameraViewMatrix, model.cameraProjectionMatrix)
    }

    private fun createTouchAnchors(camera: Camera, frame: Frame, currentModel: PolyAssetRenderer) {
        tapHelper.poll()?.apply {
            if (camera.trackingState == TrackingState.TRACKING) {
                for (hit in frame.hitTest(this)) {
                    // Check if any plane was hit, and if it was hit inside the plane polygon
                    val trackable = hit.trackable
                    // Creates an anchor if a plane or an oriented point was hit.
                    if (trackable is Plane && trackable.isPoseInPolygon(hit.hitPose) ||
                            trackable is Point && trackable.orientationMode == Point.OrientationMode.ESTIMATED_SURFACE_NORMAL) {
                        // Hits are sorted by depth. Consider only closest hit on a plane or oriented point.
                        // Cap the number of objects created. This avoids overloading both the
                        // rendering system and ARCore.
                        if (anchors.size >= 20) {
                            val toDelete = anchors.entries.elementAt(0).key
                            toDelete.detach()
                            anchors.remove(toDelete)
                        }
                        // Adding an Anchor tells ARCore that it should track this position in
                        // space. This anchor is created on the Plane to place the 3D model
                        // in the correct position relative both to the world and to the plane.
                        val anchor = hit.createAnchor()
                        anchors[anchor] = currentModel
                        debugViewDisplayer.append(String.format("Anchor created: %.2f, %.2f, %.2f", anchor.pose.xAxis[0], anchor.pose.yAxis[0], anchor.pose.zAxis[0]))
                        break
                    }
                }
            }
        }

    }

    private fun addVirtualObjectModelToAnchor(frame: Frame, cameraViewMatrix: FloatArray, cameraProjectionMatrix: FloatArray) {
        // Visualize anchors created by touch.
        // Compute lighting from average intensity of the image.
        // The first three components are color scaling factors.
        // The last one is the average pixel intensity in gamma space.
        val colorCorrectionRgba = FloatArray(4)
        frame.lightEstimate.getColorCorrection(colorCorrectionRgba, 0)

        for (entry in anchors) {
            val anchor = entry.key
            if (anchor.trackingState != TrackingState.TRACKING) {
                continue
            }
            // Get the current pose of an Anchor in world space. The Anchor pose is updated
            // during calls to session.update() as ARCore refines its estimate of the world.
            anchor.pose.toMatrix(anchorMatrix, 0)
            val renderer = entry.value
            // Update and draw the model and its shadow.
            renderer.initIfNeeded(context)
            renderer.updateModelMatrix(anchorMatrix, 0.5f)
            renderer.draw(cameraViewMatrix, cameraProjectionMatrix, colorCorrectionRgba)
        }
    }

}
