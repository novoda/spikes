package com.novoda.spikes.arcore.poly

import okhttp3.*
import java.io.IOException
import java.util.*

class FormatDownloader(val format: ApiFormat) {

    private val client = OkHttpClient()

    enum class State {
        STATE_NOT_STARTED,
        STATE_DOWNLOADING,
        STATE_SUCCESS,
        STATE_ERROR
    }

    interface CompletionListener {
        fun onDownloadFinished(format: Format)
        fun onError(error: Exception)
    }

    var state = State.STATE_NOT_STARTED

    private val resourcesDownloaded = ArrayList<File>()

    private var rootFile: File? = null

    private val resourcesToDownload
        get() = format.resources!!.filter { it.relativePath.endsWith(".png") }

    private val downloadIsOver
        get() = resourcesDownloaded.size == resourcesToDownload.size && rootFile != null

    fun start(listener: CompletionListener) {
        state = State.STATE_DOWNLOADING
        val root = format.root
        downloadFile(
                root,
                { rootFile = it },
                { listener.onError(it) },
                { listener.onDownloadFinished(Format(rootFile!!, resourcesDownloaded, format.formatType)) }
        )
        resourcesToDownload.forEach { apiFile: ApiFile ->
            downloadFile(
                    apiFile,
                    { resourcesDownloaded.add(it) },
                    { listener.onError(it) },
                    { listener.onDownloadFinished(Format(rootFile!!, resourcesDownloaded, format.formatType)) }
            )
        }
    }

    private fun downloadFile(apiFile: ApiFile, success: (File) -> Unit, failure: (Exception) -> Unit, over: () -> Unit) {
        val request = Request.Builder()
                .url(apiFile.url)
                .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                if (state != State.STATE_DOWNLOADING) {
                    return
                }
                val responseBody = response.body()
                if (responseBody == null) {
                    failure(Exception("No file content for file " + apiFile.relativePath))
                } else {
                    success(File(apiFile.relativePath, apiFile.url, apiFile.contentType, responseBody.bytes()))
                    if (downloadIsOver) {
                        state = State.STATE_SUCCESS
                        over()
                    }
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                state = State.STATE_ERROR
                failure(e)
            }
        })
    }

}
