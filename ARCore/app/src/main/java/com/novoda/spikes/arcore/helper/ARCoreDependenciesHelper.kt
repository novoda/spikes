package com.novoda.spikes.arcore.helper

import android.app.Activity
import com.google.ar.core.ArCoreApk
import com.google.ar.core.exceptions.*

object ARCoreDependenciesHelper {
    class Result(val isARCoreInstalled: Boolean, val message: String)

    private var shouldRequestARCoreInstall = true

    fun isARCoreIsInstalled(activity: Activity): Result {
        try {
            val requestInstall = ArCoreApk.getInstance().requestInstall(activity, shouldRequestARCoreInstall)
            // We only care about 'INSTALL_REQUESTED'. Nothing to do if the returned value is 'INSTALLED'
            if (requestInstall == ArCoreApk.InstallStatus.INSTALL_REQUESTED) {
                shouldRequestARCoreInstall = false
                return Result(false, "ARCore SDK install requested")
            }

        } catch (exception: Exception) {
            return Result(false, messageFromExceptionType(exception))
        }
        return Result(true, "ARCore SDK installed")
    }

    private fun messageFromExceptionType(exception: Exception): String {
        return when (exception) {
            is UnavailableUserDeclinedInstallationException -> "Please install ARCore"
            is UnavailableArcoreNotInstalledException -> "Please install ARCore"
            is UnavailableApkTooOldException -> "Please update ARCore"
            is UnavailableSdkTooOldException -> "Please update this app"
            is UnavailableDeviceNotCompatibleException -> "This device does not support AR"
            else -> "Failed to create AR session"
        }
    }
}
