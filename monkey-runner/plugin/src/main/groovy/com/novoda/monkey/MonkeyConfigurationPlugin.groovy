package com.novoda.monkey;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class MonkeyConfigurationPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        def runMonkeyAllTask = project.task('runMonkeyAll')

        def android = project.extensions.findByName("android")
        android.command.devices().eachWithIndex { device, index ->

            def showOverlayTask = project.task("showOverlayDevice${index}", type: NotificationBarOverlay) {
                show = true
                deviceId = device.id
            }

            def monkeyTask = project.task("runMonkeyDevice${index}", type: TargetedMonkey, dependsOn: 'installDebug') {
                packageName = "com.novoda.monkey"
                events = 10000
                deviceId = device.id
                logFileName = 'monkey.log'
                categories = ["android.intent.category.MONKEY"]
            }

            def hideOverlay = project.task("hideOverlayDevice${index}", type: NotificationBarOverlay) {
                show = false
                deviceId = device.id
            }

            monkeyTask.dependsOn showOverlayTask
            monkeyTask.finalizedBy hideOverlay
            runMonkeyAllTask.dependsOn monkeyTask
        }
    }
}
