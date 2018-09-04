package com.novoda.spikes.arcore.poly

import android.net.Uri
import com.squareup.moshi.KotlinJsonAdapterFactory
import com.squareup.moshi.Moshi
import okhttp3.*
import java.io.IOException

class PolyApi {

    companion object {
        private const val HOST = "poly.googleapis.com"
        private const val VERSION = "v1"
        private const val API_KEY = "API_KEY_HERE"
    }

    private val client = OkHttpClient()
    private val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

    fun findAsset(keywords: String, listener: APIAssetListener) {

        val request = Request.Builder()
                .url(urlForKeywords(keywords))
                .build()

        val response = client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                val adapter = moshi.adapter(ApiAssets::class.java)
                var assets: List<ApiAsset>? = null
                try {
                    val body = response.body()?.string()
                    if (body == null) {
                        listener.onError(IOException("Empty body returned"))
                        return
                    }
                    assets = adapter.fromJson(body)?.assets
                } catch (error: IOException) {
                    listener.onError(error)
                    return
                }
                if (assets == null || assets.isEmpty()) {
                    listener.onAssetNotFound()
                    return
                }
                val pair = findCompatibleAssetFormat(assets)
                if (pair == null) {
                    listener.onAssetNotFound()
                } else {
                    listener.onAssetFound(pair.first, pair.second)
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                listener.onError(e)
            }
        })
    }

    private fun findCompatibleAssetFormat(assets: List<ApiAsset>): Pair<ApiAsset, ApiFormat>? {
        return assets.fold(null, ::findFirstCompatibleAssetFormat)
    }

    private fun findFirstCompatibleAssetFormat(acc: Pair<ApiAsset, ApiFormat>?, asset: ApiAsset): Pair<ApiAsset, ApiFormat>? {
        if (acc != null) {
            return acc
        }
        val format = asset.formats.find { isFormatCompatible(it) }
        return if (format != null) {
            Pair(asset, format)
        } else {
            acc
        }
    }

    private fun isFormatCompatible(format: ApiFormat) =
            format.formatType == "OBJ" && format.resources?.any { it.relativePath.endsWith(".png") } == true

    public interface APIAssetListener {
        fun onAssetFound(asset: ApiAsset, format: ApiFormat)
        fun onAssetNotFound()
        fun onError(error: Exception)
    }

    private fun urlForKeywords(keywords: String) = Uri.Builder()
            .scheme("https")
            .authority(HOST)
            .appendPath(VERSION)
            .appendPath("assets")
            .appendQueryParameter("maxComplexity", "SIMPLE")
            .appendQueryParameter("pageSize", "100")
            .appendQueryParameter("keywords", keywords)
            .appendQueryParameter("format", "OBJ")
            .appendQueryParameter("key", API_KEY)
            .build().toString()

}
