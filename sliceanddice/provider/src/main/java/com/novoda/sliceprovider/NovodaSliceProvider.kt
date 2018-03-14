package com.novoda.sliceprovider

import android.net.Uri
import android.util.Log
import androidx.slice.Slice
import androidx.slice.SliceProvider

class NovodaSliceProvider : SliceProvider() {

    override fun onBindSlice(sliceUri: Uri): Slice {
        Log.i("SliceProvider", "Creating slices")

        return when (sliceUri.path) {
            "yucca" -> createYuccaSlice(context, sliceUri)
            "search" -> createSearchSlice(context, sliceUri)
            else -> createDemoSlice(context, sliceUri)
        }
    }

    override fun onCreateSliceProvider(): Boolean {
        Log.i("SliceProvider", "Creating slice provider")
        return true
    }
}
