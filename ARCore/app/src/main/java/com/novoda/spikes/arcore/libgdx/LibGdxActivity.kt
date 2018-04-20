package com.novoda.spikes.arcore.libgdx

import android.os.Bundle
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration
import com.novoda.spikes.arcore.libgdx.gdx.BaseARCoreActivity

class LibGdxActivity : BaseARCoreActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val config = AndroidApplicationConfiguration()
        // Setting this to true will make the view use the complete UI, hiding all the decor such as
        // Snackbars, etc.  If you have no other UI, it looks nice to be immersive.
        config.useImmersiveMode = true
        initialize(LibGdxArCoreScene(), config)
    }
}
