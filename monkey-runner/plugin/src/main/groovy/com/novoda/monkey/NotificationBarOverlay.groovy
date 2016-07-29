package com.novoda.monkey

import com.novoda.gradle.command.AdbTask
import org.gradle.api.tasks.TaskAction

class NotificationBarOverlay extends AdbTask {

    boolean show

    @TaskAction
    void show() {
        assertDeviceAndRunCommand(["shell", "am", "startservice", "-a", "com.novoda.monkeytrap.SHOW_OVERLAY", "--ez", "show", show])
    }

    @Override
    protected handleCommandOutput(def text) {
        super.handleCommandOutput(text)
        if (text.contains("Error")) {
            throw new GroovyRuntimeException('Failed to trigger Overlay visibility')
        }
    }
}
