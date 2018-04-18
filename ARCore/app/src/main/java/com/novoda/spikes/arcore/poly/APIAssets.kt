package com.novoda.spikes.arcore.poly

data class APIAssets(val assets: List<APIAsset>?)

data class APIAsset(val name: String, val displayName: String, val authorName: String, val formats: List<APIFormat>)

data class APIFormat(val root: APIFile, val resources: List<APIFile>?, val formatType: String)

data class APIFile(val relativePath: String, val url: String, val contentType: String)
