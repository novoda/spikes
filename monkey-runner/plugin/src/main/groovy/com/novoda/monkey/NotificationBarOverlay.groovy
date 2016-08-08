package com.novoda.monkey

import com.novoda.gradle.command.AdbTask
import org.gradle.api.tasks.TaskAction

class NotificationBarOverlay extends AdbTask {

    boolean show

    /**
     * Executes shell command <i>startservice</i> to toggle MonkeyTrap visibility
     * <p>
     * This command needs <code>--user 0</code> flag to run correctly in certain devices (like Samsung Galaxy S3)
     * The reason is user permissions. By default command would be executed by "user 2" and will fail due lack of permissions.
     */
    @TaskAction
    void show() {
        assertDeviceAndRunCommand(["shell", "am", "startservice", "--user", "0", "-a", "com.novoda.monkeytrap.SHOW_OVERLAY", "--ez", "show", show])
    }

    @Override
    protected handleCommandOutput(def text) {
        super.handleCommandOutput(text)
        if (text.contains("Error")) {
            throw new GroovyRuntimeException('Failed to trigger Overlay visibility')
        }
    }
}
