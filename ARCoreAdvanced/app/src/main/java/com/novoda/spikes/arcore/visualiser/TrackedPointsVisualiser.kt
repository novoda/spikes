package com.novoda.spikes.arcore.visualiser

import android.content.Context
import com.novoda.spikes.arcore.google.rendering.PointCloudRenderer
import com.novoda.spikes.arcore.rendering.ARCoreDataModel

class TrackedPointsVisualiser(private val context: Context) {
    private val pointCloudRenderer = PointCloudRenderer()

    fun init() {
        pointCloudRenderer.createOnGlThread(context)
    }

    fun drawTrackedPoints(model: ARCoreDataModel) {
        val pointCloud = model.frame.acquirePointCloud()
        pointCloudRenderer.update(pointCloud)
        pointCloudRenderer.draw(model.cameraViewMatrix, model.cameraProjectionMatrix)
        // Application is responsible for releasing the point cloud resources after using it.
        pointCloud.release()
    }
}
