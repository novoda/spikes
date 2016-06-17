package com.odai.architecturedemo.imageloader

import android.widget.ImageView
import com.bumptech.glide.DrawableRequestBuilder
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.load.resource.drawable.GlideDrawable
import com.bumptech.glide.request.target.Target
import java.net.URI
import java.util.*

fun load(uri: URI, init: ImageRequestBuilder.() -> Unit): ImageRequestBuilder {
    val imageLoader = ImageRequestBuilder(uri)
    imageLoader.init()
    return imageLoader
}

class ImageRequestBuilder(internal val uri: URI) {

    internal var imageView: ImageView? = null
    internal var crop: Crop? = null

    fun cropAs(defineCrop: ImageRequestBuilder.() -> Crop): ImageRequestBuilder {
        crop = defineCrop()
        return this
    }

    fun into(init: ImageRequest.() -> ImageView) {
        val request = ImageRequest()
        imageView = request.init()
        request.target = load()
    }

    internal fun load(): Target<GlideDrawable> {
        val context = imageView!!.context
        return Glide.with(context)
                .load(uri)
                .transform(
                        when (crop) {
                            Crop.CENTER_CROP -> arrayOf(CenterCrop(context))
                            Crop.CIRCLE_CROP -> arrayOf(CenterCrop(context), CircleCrop(context))
                            Crop.FIT_CENTER -> arrayOf(FitCenter(context))
                            else -> arrayOf()
                        }
                )
                .into(imageView)
    }

    fun <T> DrawableRequestBuilder<T>.transform(array: Array<out BitmapTransformation>): DrawableRequestBuilder<T> {
        if(array.isEmpty()) {
            return this;
        } else {
            return this.transform(*array)
        }
    }

}

class ImageRequest() {

    internal var target: Target<GlideDrawable>? = null

    fun cancel() {
        target?.request?.clear()
    }

}

enum class Crop {
    CENTER_CROP,
    FIT_CENTER,
    CIRCLE_CROP
}

