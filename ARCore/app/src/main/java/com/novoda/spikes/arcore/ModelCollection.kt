package com.novoda.spikes.arcore

import android.util.Log
import com.novoda.spikes.arcore.poly.PolyAsset
import com.novoda.spikes.arcore.poly.PolyAssetLoader
import com.novoda.spikes.arcore.rendering.PolyAssetRenderer

class ModelCollection(
        private val assetLoader: PolyAssetLoader,
        private val messageDisplayer: MessageDisplayer,
        private val modelsDisplayer: ModelsDisplayer
) : ARClassifier.Listener {

    private val models = HashMap<String, PolyAssetRenderer>()

    private val modelLabels : List<String>
        get() = models.keys.toList()
    
    var currentModel: PolyAssetRenderer? = null

    private var modelModulo = 0

    override fun onObjectClassified(label: String) {
        if (!models.containsKey(label) && modelModulo == 0) { //We start with a basic modulo, we could improve with a reliability based load
            loadAssetFor(label)
        }
        modelModulo = (modelModulo + 1) % 30
    }

    fun selectModel(model: String) {
        currentModel = models[model]
    }

    private fun loadAssetFor(label: String) {
        assetLoader.loadAssetFor(label, object : PolyAssetLoader.AssetListener {
            override fun onAssetFound(asset: PolyAsset) {
                messageDisplayer.showMessage("Loaded model: ${asset.displayName} by ${asset.authorName}")
                models[label] = PolyAssetRenderer(asset)
                modelsDisplayer.displayModels(modelLabels)
            }

            override fun onAssetNotFound() {
                messageDisplayer.showMessage("No model found for: $label")
            }

            override fun onError(error: Exception) {
                Log.e("ARCore", "Failed to load asset for: $label", error)
                messageDisplayer.showMessage("Failed to load asset for: $label ${error.message}")
            }
        })
    }
}
