package com.novoda.android.upgradedemo

import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.ActivityResult.RESULT_IN_APP_UPDATE_FAILED
import com.google.android.play.core.install.model.AppUpdateType.FLEXIBLE
import com.google.android.play.core.install.model.AppUpdateType.IMMEDIATE
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val appUpdateManager by lazy { AppUpdateManagerFactory.create(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onResume() {
        super.onResume()
        appUpdateManager.appUpdateInfo.addOnCompleteListener { task ->
            loading_view.visibility = View.GONE
            if (task.isSuccessful) {
                val info = task.result
                info_view.text = info.values
                ready_views.visibility = View.VISIBLE
                update_button.setOnClickListener {
                    try {
                        if (!startImmediateUpdate(info)) {
                            showError("startImmediateUpdate returned false")
                        }
                    } catch (e: IntentSender.SendIntentException) {
                        log( "Failed to get update task", e)
                        showError("Failed: ${e.message}")
                    }
                }
            } else {
                log( "Failed to get update task", task.exception)
                showError(task.exception.message)
            }
        }

    }

    private fun showError(message: String?) {
        error_view.text = message
        error_view.visibility = View.VISIBLE
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == REQUEST_CODE) {
            when(resultCode) {
                Activity.RESULT_OK -> showError("OK")
                Activity.RESULT_CANCELED -> showError("User Cancelled")
                RESULT_IN_APP_UPDATE_FAILED -> showError("Update failed: $data")
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun startImmediateUpdate(info: AppUpdateInfo) =
        appUpdateManager.startUpdateFlowForResult(info, IMMEDIATE, this, REQUEST_CODE)

    private fun log(message: String, e: Throwable?) {
        Log.e("UPGRADE", message, e)
    }

}

const val REQUEST_CODE = 101

private val AppUpdateInfo.values
    get() = """
        updateAvailability = ${updateAvailability().asAvailabilityString()}
        isUpdateTypeAllowed(IMMEDIATE)" = ${isUpdateTypeAllowed(IMMEDIATE)}
        isUpdateTypeAllowed(FLEXIBLE)" = ${isUpdateTypeAllowed(FLEXIBLE)}
        installStatus = ${installStatus().asInstallStatusString()}
        packageName = ${packageName()}
        """.trimIndent()

private fun Int.asAvailabilityString() = when (this) {
    UpdateAvailability.UNKNOWN -> "UNKNOWN"
    UpdateAvailability.UPDATE_NOT_AVAILABLE -> "UPDATE_NOT_AVAILABLE"
    UpdateAvailability.UPDATE_AVAILABLE -> "UPDATE_AVAILABLE "
    UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS -> "DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS"
    else -> "Not valid: $this"
}

private fun Int.asInstallStatusString() = when (this) {
    InstallStatus.UNKNOWN -> "UNKNOWN"
    InstallStatus.REQUIRES_UI_INTENT -> "REQUIRES_UI_INTENT"
    InstallStatus.PENDING -> "PENDING"
    InstallStatus.DOWNLOADING -> "DOWNLOADING"
    InstallStatus.DOWNLOADED -> "DOWNLOADED"
    InstallStatus.INSTALLING -> "INSTALLING"
    InstallStatus.INSTALLED -> "INSTALLED"
    InstallStatus.FAILED -> "FAILED"
    InstallStatus.CANCELED -> "CANCELED"
    else -> "Not valid: $this"
}


