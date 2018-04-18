package com.novoda.spikes.arcore

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import com.novoda.spikes.arcore.google.rendering.TextureReader
import com.novoda.spikes.arcore.google.rendering.TextureReaderImage
import com.novoda.spikes.arcore.google.tensorflow.ImageClassifier

class ARClassifier(private val context: Context, private val classifier: ImageClassifier, private val listener: Listener) {

    private val textureReader = TextureReader()
    private var gpuDownloadFrameBufferIndex = -1

    fun init() {
        textureReader.create(
                context,
                TextureReaderImage.IMAGE_FORMAT_I8,
                classifier.imageSizeX,
                classifier.imageSizeY,
                false
        )
    }

    fun handleFrame(textureId: Int) {
        classifyImageFromGPU(textureId)
    }

    fun close() {
        classifier.close()
    }

    interface Listener {
        fun onObjectClassified(label: String)
    }

    private fun classifyImageFromGPU(textureId: Int) {
        // If there is a frame being requested previously, acquire the pixels and process it.
        if (gpuDownloadFrameBufferIndex >= 0) {
            val image = textureReader.acquireFrame(gpuDownloadFrameBufferIndex)

            if (image.format != TextureReaderImage.IMAGE_FORMAT_I8) {
                throw IllegalArgumentException(
                        "Expected image in I8 format, got format " + image.format)
            }

            classifyImage(image)

            // You should always release frame buffer after using. Otherwise the next call to
            // submitFrame() may fail.
            textureReader.releaseFrame(gpuDownloadFrameBufferIndex)

        }

        // Submit request for the texture from the current frame.
        gpuDownloadFrameBufferIndex = textureReader.submitFrame(textureId, classifier.getImageSizeX(), classifier.getImageSizeY())
    }

    private var frameModulo = 0

    private fun classifyImage(image: TextureReaderImage) {
        if (frameModulo == 0) {
            Thread(Runnable {
                //use image.buffer
                val label = classifier.classifyFrame(image)
                Log.e(TAG, "Got result:\n$label")
                listener.onObjectClassified(label)
            }).start()
        }
        frameModulo = (frameModulo + 1) % 5
    }

}
