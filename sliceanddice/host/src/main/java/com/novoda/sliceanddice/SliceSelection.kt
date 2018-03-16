package com.novoda.sliceanddice

import android.net.Uri
import android.view.View
import android.widget.AdapterView

internal val baseSliceUri: Uri = Uri.parse("content://com.novoda.sliceprovider/")

internal val sliceChoices = listOf(
    SliceChoice(baseSliceUri.buildUpon().appendPath("demo").build(), "Slice demo"),
    SliceChoice(baseSliceUri.buildUpon().appendPath("yucca").build(), "Image search results"),
    SliceChoice(baseSliceUri.buildUpon().appendPath("search").build(), "Search results")
)

internal data class SliceChoice(val uri: Uri, val text: String) {

    override fun toString() = text
}

internal fun sliceSelectionListener(
    onItemSelected: (adapterView: AdapterView<*>, selectedView: View, selectedPosition: Int, selectedId: Long) -> Unit,
    onNothingSelected: ((adapterView: AdapterView<*>) -> Unit)? = null
) = object : AdapterView.OnItemSelectedListener {

    override fun onNothingSelected(adapterView: AdapterView<*>) {
        onNothingSelected?.invoke(adapterView)
    }

    override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, id: Long) {
        onItemSelected.invoke(adapterView, view, position, id)
    }
}
