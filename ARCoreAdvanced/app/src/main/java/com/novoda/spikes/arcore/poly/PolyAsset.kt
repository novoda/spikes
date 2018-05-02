package com.novoda.spikes.arcore.poly


data class PolyAsset(
        val name: String,
        val representsLabel: String,
        val displayName: String,
        val authorName: String,
        val format: Format,
        val thumbnail: String?
)

data class Format(val root: File, val resources: List<File>, val formatType: String)

data class File(val relativePath: String, val url: String, val contentType: String, val content: ByteArray)
