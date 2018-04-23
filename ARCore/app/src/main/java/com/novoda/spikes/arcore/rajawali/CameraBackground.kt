/*
 * Copyright 2018 eje inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.novoda.spikes.arcore.rajawali

import android.graphics.Color
import android.util.Log
import org.rajawali3d.materials.Material
import org.rajawali3d.materials.textures.StreamingTexture
import org.rajawali3d.math.vector.Vector3
import org.rajawali3d.primitives.ScreenQuad

/**
 * This class renders the AR background from camera feed. It creates and hosts the texture given to
 * ARCore to be filled with the camera image.
 */
class CameraBackground : ScreenQuad(1, 1) {

    val texture = StreamingTexture("backgroundTexture", {
        // This callback is for setSurface or something but ARCore handles texture directly so Surface is not used.
        Log.d(TAG, "Texture is created")
    })

    init {
        material = Material().apply {
            addTexture(texture)
            color = Color.BLACK
        }

        rotate(Vector3.Axis.Z, 90.0)
    }

    companion object {
        const val TAG = "CameraBackground"
    }
}