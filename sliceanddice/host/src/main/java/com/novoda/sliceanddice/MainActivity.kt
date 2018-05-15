package com.novoda.sliceanddice

import android.net.Uri
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.slice.SliceManager
import androidx.slice.widget.SliceLiveData
import com.novoda.sliceshost.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.settings_card.*

class MainActivity : AppCompatActivity() {

    private lateinit var sliceManager: SliceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sliceManager = SliceManager.getInstance(this)
        sliceSelector.adapter = ArrayAdapter<SliceChoice>(this, android.R.layout.simple_list_item_1, sliceChoices)
    }

    override fun onStart() {
        super.onStart()

        if (providerAppNotInstalled(packageManager, baseSliceUri.authority)) {
            showMissingProviderDialog(this, { finish() }, baseSliceUri)
            return
        }

        sliceSelector.onItemSelectedListener = sliceSelectionListener({ _, _, selectedPosition, _ ->
            onSliceSelectionChanged(sliceChoices[selectedPosition].uri)
        })

        sliceSelector.setSelection(0)
    }

    private fun onSliceSelectionChanged(selectedSliceUri: Uri) {
        startObservingSliceLiveData(selectedSliceUri)
    }

    private fun startObservingSliceLiveData(sliceUri: Uri) {
        val slice = sliceManager.bindSlice(sliceUri)
        sliceView.slice = slice

        SliceLiveData.fromUri(this, sliceUri)
            .observe(this, Observer({ sliceResult ->
                sliceView.slice = sliceResult
                invalidateOptionsMenu()
            }))
    }
}
