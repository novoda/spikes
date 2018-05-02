package com.novoda.spikes.arcore

import android.opengl.GLSurfaceView
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.google.ar.core.Session
import com.google.ar.core.exceptions.CameraNotAvailableException
import com.novoda.spikes.arcore.google.helper.TapHelper
import com.novoda.spikes.arcore.helper.ARCoreDependenciesHelper
import com.novoda.spikes.arcore.helper.ARCoreDependenciesHelper.Result.Failure
import com.novoda.spikes.arcore.helper.ARCoreDependenciesHelper.Result.Success
import com.novoda.spikes.arcore.helper.CameraPermissionHelper
import com.novoda.spikes.arcore.poly.PolyAsset
import com.novoda.spikes.arcore.poly.PolyAssetLoader
import com.novoda.spikes.arcore.rendering.NovodaSurfaceViewRenderer
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var messageHandler: Handler;

    private var session: Session? = null
    private val renderer: NovodaSurfaceViewRenderer by lazy {
        NovodaSurfaceViewRenderer(this, debugViewDisplayer, tapHelper)
    }
    private val debugViewDisplayer: DebugViewDisplayer by lazy {
        DebugViewDisplayer(debugTextView)
    }
    private val tapHelper: TapHelper by lazy {
        TapHelper(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        messageHandler = Handler()
        setupSurfaceView()
        loadAssetFor("parrot")
    }

    private fun loadAssetFor(keywords: String) {
        PolyAssetLoader().loadAssetFor(keywords, object : PolyAssetLoader.AssetListener {
            override fun onAssetFound(asset: PolyAsset) {
                showMessage("Loaded model: ${asset.displayName} by ${asset.authorName}")
                renderer.setModel(asset)
            }

            override fun onAssetNotFound() {
                showMessage("Not model found for: $keywords")
            }

            override fun onError(error: Exception) {
                Log.e("ARCore", "Failed to load asset for: $keywords", error)
                showMessage("Failed to load asset for: $keywords ${error.message}")
            }
        })
    }

    private fun setupSurfaceView() {
        surfaceView.preserveEGLContextOnPause = true
        surfaceView.setEGLContextClientVersion(2)
        surfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0) // Alpha used for plane blending.
        surfaceView.setRenderer(renderer)
        surfaceView.renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
        surfaceView.setOnTouchListener(tapHelper)
    }

    override fun onResume() {
        super.onResume()
        checkAREnvironment()
    }

    private fun checkAREnvironment() {
        ARCoreDependenciesHelper.isARCoreIsInstalled(this).apply {
            when {
                this is Failure -> showMessage(message)
                this === Success && CameraPermissionHelper.isCameraPermissionGranted(this@MainActivity) -> createOrResumeARSession()
            }
        }
    }

    private fun createOrResumeARSession() {
        if (session == null) {
            session = Session(this).apply {
                renderer.setSession(this)
            }

        }
        // Note that order matters - see the note in onPause(), the reverse applies here.
        try {
            session?.resume()
        } catch (e: CameraNotAvailableException) {
            // In some cases the camera may be given to a different app instead. Recreate the session at the next iteration.
            showMessage("Camera not available. Please restart the app.")
            return
        }
        surfaceView.onResume()
    }

    public override fun onPause() {
        super.onPause()
        if (session != null) {
            // Note that the order matters - GLSurfaceView is paused first so that it does not try
            // to query the session. If Session is paused before GLSurfaceView, GLSurfaceView may
            // still call session.update() and get a SessionPausedException.
            surfaceView.onPause()
            session?.pause()
        }
    }

    private fun showMessage(message: String) {
        messageHandler.post {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }

}
