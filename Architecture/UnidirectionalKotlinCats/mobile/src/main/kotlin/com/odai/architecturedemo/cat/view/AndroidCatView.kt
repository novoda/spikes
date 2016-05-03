package com.odai.architecturedemo.cat.view

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.odai.architecturedemo.R
import com.odai.architecturedemo.cat.model.Cat
import com.odai.architecturedemo.imageloader.Crop
import com.odai.architecturedemo.imageloader.load

class AndroidCatView(context: Context, attrs: AttributeSet): CatView, ImageView(context, attrs) {

    override fun display(cat: Cat) {
        val target = this
        load(cat.image) {
            cropAs { Crop.FIT_CENTER }
            into { target }
        }
    }

}
