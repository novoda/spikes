package com.odai.architecturedemo.imageloader

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.module.GlideModule
import java.io.InputStream
import java.net.URI

class GlideConfiguration: GlideModule {

    override fun applyOptions(context: Context, builder: GlideBuilder) {
        builder.setDecodeFormat(DecodeFormat.PREFER_ARGB_8888)
    }

    override fun registerComponents(context: Context, glide: Glide) {
        glide.register(URI::class.java, InputStream::class.java, URIGlideLoader.Factory())
    }
}
