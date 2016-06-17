package com.odai.architecturedemo.imageloader

import android.content.Context
import com.bumptech.glide.load.model.GenericLoaderFactory
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.load.model.ModelLoaderFactory
import com.bumptech.glide.load.model.stream.BaseGlideUrlLoader
import java.io.InputStream
import java.net.URI

class URIGlideLoader(context: Context) : BaseGlideUrlLoader<URI>(context) {

    override fun getUrl(model: URI?, width: Int, height: Int) = model.toString()

    class Factory: ModelLoaderFactory<URI, InputStream> {

        override fun build(context: Context, factories: GenericLoaderFactory): ModelLoader<URI, InputStream> {
            return URIGlideLoader(context)
        }

        override fun teardown() {
            //Nothing to do
        }

    }

}
