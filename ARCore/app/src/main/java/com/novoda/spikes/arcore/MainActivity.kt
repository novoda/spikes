package com.novoda.spikes.arcore

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MotionEvent
import android.widget.Toast
import com.google.ar.core.Session
import com.novoda.spikes.arcore.google.helper.TapHelper
import com.novoda.spikes.arcore.helper.ARCoreDependenciesHelper
import com.novoda.spikes.arcore.helper.ARCoreDependenciesHelper.Result.Failure
import com.novoda.spikes.arcore.helper.ARCoreDependenciesHelper.Result.Success
import com.novoda.spikes.arcore.helper.CameraPermissionHelper
import com.novoda.spikes.arcore.rendering.NovodaARCoreRajawaliRenderer
import kotlinx.android.synthetic.main.activity_main.*
import org.rajawali3d.view.ISurface

class MainActivity : AppCompatActivity() {
    private var session: Session? = null
    private lateinit var render: NovodaARCoreRajawaliRenderer
    private val debugViewDisplayer: DebugViewDisplayer by lazy {
        DebugViewDisplayer(debugTextView)
    }
    private val tapHelper: TapHelper by lazy {
        TapHelper(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupSurfaceView()
    }

    private fun setupSurfaceView() {
//        surfaceView.setSurfaceRenderer(renderer)
        surfaceView.setOnTouchListener(tapHelper)
        surfaceView.renderMode = ISurface.RENDERMODE_WHEN_DIRTY
        surfaceView.setFrameRate(60.0)

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
                render = NovodaARCoreRajawaliRenderer(this@MainActivity, tapHelper, debugViewDisplayer, this)
                surfaceView.setSurfaceRenderer(render)

            }

        }
        render.onResume()
        surfaceView.onResume()
    }

    public override fun onPause() {
        super.onPause()
        if (session != null) {
            // Note that the order matters - GLSurfaceView is paused first so that it does not try
            // to query the session. If Session is paused before GLSurfaceView, GLSurfaceView may
            // still call session.update() and get a SessionPausedException.
            surfaceView.onPause()
            render.onPause()
        }
    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        render.onTouchEvent(event)
        return super.onTouchEvent(event)
    }

}
