package com.novoda.spikes.arcore.visualiser

import android.content.Context
import com.google.ar.core.Frame
import com.novoda.spikes.arcore.rendering.PointCloudRenderer

class TrackedPointsVisualiser(private val context: Context) {
    private val pointCloudRenderer = PointCloudRenderer()

    fun init() {
        pointCloudRenderer.createOnGlThread(context)
    }

    fun visualiseTrackedPoints(model: ARCoreDataModel) {
        visualiseTrackedPoints(model.frame, model.cameraViewMatrix, model.cameraProjectionMatrix)
    }
    fun visualiseTrackedPoints(frame: Frame, viewmtx: FloatArray, projmtx: FloatArray) {
        // Visualize tracked points.
        val pointCloud = frame.acquirePointCloud()
        pointCloudRenderer.update(pointCloud)
        pointCloudRenderer.draw(viewmtx, projmtx)
        // Application is responsible for releasing the point cloud resources after using it.
        pointCloud.release()
    }
}
