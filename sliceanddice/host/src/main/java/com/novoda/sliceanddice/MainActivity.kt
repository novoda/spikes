package com.novoda.sliceanddice

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.constraint.ConstraintSet
import android.support.v7.app.AppCompatActivity
import android.transition.TransitionManager
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.slice.SliceManager
import com.novoda.sliceshost.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.settings_card.*

class MainActivity : AppCompatActivity() {

    private lateinit var sliceManager: SliceManager
    private var pendingSliceUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sliceManager = SliceManager.getInstance(this)
        sliceSelector.adapter = ArrayAdapter<SliceChoice>(this, android.R.layout.simple_list_item_1, sliceChoices)

        transitionUiTo(UiState.Empty, animate = false)
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
        tryShowingSlice(selectedSliceUri)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        handleSlicePermissionActivityResult(requestCode, onSlicePermissionResult = {
            if (pendingSliceUri == null) {
                throw IllegalArgumentException("The slice URI to bind to cannot be null")
            }
            tryShowingSlice(pendingSliceUri!!)
            pendingSliceUri = null
        })
    }

    private fun tryShowingSlice(sliceUri: Uri) {
        if (sliceManager.missingPermission(sliceUri, appName = getString(R.string.app_name))) {
            transitionUiTo(UiState.NeedPermission)
            permissionCta.setOnClickListener {
                pendingSliceUri = sliceUri
                sliceManager.requestPermission(sliceUri, this)
            }
        } else {
            startObservingSliceLiveData(sliceUri)
        }
    }

    private fun startObservingSliceLiveData(sliceUri: Uri) {
        transitionUiTo(UiState.SliceContent)

        val slice = sliceManager.bindSlice(sliceUri)
        sliceView.setSlice(slice)

        if (sliceUri.path == sliceChoices[0].uri.path) {
            Toast.makeText(
                this,
                "The demo slice has an InputRange. Using an InputRange triggers its action " +
                    "immediately whenever the slice is set on the SliceView. This is a bug in " +
                    "Slices, sorry.",
                Toast.LENGTH_LONG
            ).show()
        }

        // TODO due to a bug in 28.0.0-alpha1, we can't use the LiveData yet 😭
//        SliceLiveData.fromUri(this, sliceUri)
//            .observe(this, Observer({ sliceResult ->
//                sliceView.setSlice(sliceResult)
//                invalidateOptionsMenu()
//            }))
    }

    private fun transitionUiTo(uiState: UiState, animate: Boolean = true) {
        if (animate) {
            TransitionManager.beginDelayedTransition(sliceContainer)
        }

        ConstraintSet().apply {
            clone(sliceContainer)
            setVisibility(R.id.needPermissionGroup, uiState.permissionGroupVisibility)
            setVisibility(R.id.sliceView, uiState.sliceViewVisibility)
            applyTo(sliceContainer)
        }
    }

    private sealed class UiState {

        abstract val permissionGroupVisibility: Int

        abstract val sliceViewVisibility: Int

        object Empty : UiState() {
            override val permissionGroupVisibility = View.INVISIBLE
            override val sliceViewVisibility = View.INVISIBLE
        }

        object NeedPermission : UiState() {
            override val permissionGroupVisibility = View.VISIBLE
            override val sliceViewVisibility = View.INVISIBLE
        }

        object SliceContent : UiState() {
            override val permissionGroupVisibility = View.INVISIBLE
            override val sliceViewVisibility = View.VISIBLE
        }
    }
}
