package com.novoda.spikes.arcore.rendering

import android.content.Context
import android.view.Display
import android.view.WindowManager
import com.google.ar.core.Camera
import com.google.ar.core.Frame
import com.google.ar.core.Session

class ARCoreDataModel(context: Context) : BaseARCoreDataModel(context) {
    override fun isSessionReady(): Boolean {
        return session != null
    }

    override fun onCameraUpdated() {
        // no-op
    }


}
