/*
Copyright 2017 Google Inc.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    https://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package com.novoda.spikes.arcore.libgdx

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.novoda.spikes.arcore.libgdx.gdx.ObjLoaderImproved

/**
 * Model of Andy the Android. This includes loading 2 OBJ models and combining them into one model.
 * The assets are loaded using the internal file loader, so the path to the assets is relative to
 * src/main/assets in the source project.
 */
class AndyModel
/**
 * Create a new model. The asset manager is used to begin the asynchronous loading of the model
 * assets. To make sure the assets are loaded, the caller needs to add assetManager.update() to
 * the render() method.
 */
(assetManager: AssetManager) {
    private var model: Model? = null

    val isInitialized: Boolean
        get() = model != null

    init {
        assetManager.setLoader(
                Model::class.java, ".obj", ObjLoaderImproved(InternalFileHandleResolver()))
        assetManager.load(ANDY_MODEL, Model::class.java)
    }

    /**
     * Initializes the model. This needs to be called when the model assets are loaded into memory. If
     * they cannot be found yet, it is assumed that they are still loading.
     *
     * @return true when the model is initialized and ready to use.
     */
    fun initialize(assetManager: AssetManager): Boolean {
        if (assetManager.isLoaded(ANDY_MODEL, Model::class.java)) {
            val body = assetManager.get(ANDY_MODEL, Model::class.java)
            if (body != null) {
                val bodyMaterial = Material(TextureAttribute.createDiffuse(Texture(ANDY_TEXTURE)))
                val builder = ModelBuilder()
                builder.begin()
                for (part in body.meshParts) {
                    builder.part(part, bodyMaterial)
                }
                model = builder.end()
            }
        }
        return isInitialized
    }

    fun createInstance(): ModelInstance {
        return ModelInstance(model!!)
    }

    companion object {

        private val ANDY_MODEL = "models/andy.obj"
        private val ANDY_TEXTURE = "models/andy.png"
    }
}
