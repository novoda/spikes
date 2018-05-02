package com.novoda.spikes.arcore.poly


data class PolyAsset(val name: String, val displayName: String, val authorName: String, val format: Format)

data class Format(val root: File, val resources: List<File>, val formatType: String)

data class File(val relativePath: String, val url: String, val contentType: String, val content: ByteArray)
