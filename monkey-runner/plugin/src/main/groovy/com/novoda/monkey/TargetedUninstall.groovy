package com.novoda.monkey;

import com.novoda.gradle.command.AdbTask
import org.gradle.api.tasks.TaskAction

class TargetedUninstall extends AdbTask {

    String packageName

    @TaskAction
    void removeApp() {
        assertDeviceAndRunCommand(["uninstall", packageName])
    }

}
