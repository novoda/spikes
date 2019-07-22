package com.novoda.spikes.arcore.poly

data class ApiAssets(val assets: List<ApiAsset>?)

data class ApiAsset(val name: String, val displayName: String, val authorName: String, val formats: List<ApiFormat>, val thumbnail: ApiFile?)

data class ApiFormat(val root: ApiFile, val resources: List<ApiFile>?, val formatType: String)

data class ApiFile(val relativePath: String, val url: String, val contentType: String)
