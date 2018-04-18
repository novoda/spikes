package com.novoda.spikes.arcore

import android.util.Log
import com.novoda.spikes.arcore.poly.PolyAsset
import com.novoda.spikes.arcore.poly.PolyAssetLoader
import com.novoda.spikes.arcore.rendering.PolyAssetRenderer

class ModelCollection(private val assetLoader: PolyAssetLoader, private val messageDisplayer: MessageDisplayer) : ARClassifier.Listener {

    private val models = HashMap<String, PolyAssetRenderer>()
    
    var currentModel: PolyAssetRenderer? = null

    private var modelModulo = 0

    override fun onObjectClassified(label: String) {
        if (!models.containsKey(label) && modelModulo == 0) { //We start with a basic modulo, we could improve with a reliability based load
            loadAssetFor(label)
        }
        modelModulo = (modelModulo + 1) % 30
    }

    private fun loadAssetFor(label: String) {
        assetLoader.loadAssetFor(label, object : PolyAssetLoader.AssetListener {
            override fun onAssetFound(asset: PolyAsset) {
                messageDisplayer.showMessage("Loaded model: ${asset.displayName} by ${asset.authorName}")
                val polyAssetRenderer = PolyAssetRenderer(asset)
                models[label] = polyAssetRenderer
                //TODO for now we select the last detected later we want a selection panel
                currentModel = polyAssetRenderer
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
