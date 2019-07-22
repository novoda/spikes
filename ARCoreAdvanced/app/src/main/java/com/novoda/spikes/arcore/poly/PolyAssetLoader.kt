package com.novoda.spikes.arcore.poly

class PolyAssetLoader() {

    private val polyApi = PolyApi()

    fun loadAssetFor(keywords: String, listener: AssetListener) {
        polyApi.findAsset(keywords, object : PolyApi.APIAssetListener {
            override fun onAssetFound(asset: ApiAsset, format: ApiFormat) {
                FormatDownloader(format).start(object : FormatDownloader.CompletionListener {
                    override fun onDownloadFinished(format: Format) {
                        listener.onAssetFound(PolyAsset(
                                asset.name,
                                keywords,
                                asset.displayName,
                                asset.authorName,
                                format,
                                asset.thumbnail?.url
                        ))
                    }

                    override fun onError(error: Exception) {
                        listener.onError(error)
                    }
                })
            }

            override fun onAssetNotFound() {
                listener.onAssetNotFound()
            }

            override fun onError(error: Exception) {
                listener.onError(error)
            }

        })
    }

    public interface AssetListener {
        fun onAssetFound(asset: PolyAsset)
        fun onAssetNotFound()
        fun onError(error: Exception)
    }
}
