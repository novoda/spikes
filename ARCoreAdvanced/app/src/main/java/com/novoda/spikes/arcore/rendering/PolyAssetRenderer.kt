package com.novoda.spikes.arcore.rendering

import android.content.Context
import com.novoda.spikes.arcore.poly.PolyAsset
import com.novoda.spikes.arcore.poly.copied.PolyObjectRenderer

class PolyAssetRenderer(private val asset: PolyAsset) {

    private val renderer = PolyObjectRenderer()
    private var needsInit = true

    fun initIfNeeded(context: Context) {
        if (needsInit) {
            needsInit = false
            renderer.createOnGlThread(context, asset.format.root.content, asset.format.resources.first().content)
            renderer.setMaterialProperties(0.0f, 2.0f, 0.5f, 6.0f)
        }
    }

    fun updateModelMatrix(anchorMatrix: FloatArray, assetDisplaySize: Float) {
        renderer.updateModelMatrix(anchorMatrix, assetDisplaySize)
    }

    fun draw(cameraViewMatrix: FloatArray, cameraProjectionMatrix: FloatArray, colorCorrectionRgba: FloatArray) {
        renderer.draw(cameraViewMatrix, cameraProjectionMatrix, colorCorrectionRgba)
    }

}
