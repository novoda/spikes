package com.novoda.monkey

import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project

public class MonkeyConfigurationPlugin implements Plugin<Project> {

    private static final String TASK_NAME = 'runMonkeyAll'

    @Override
    public void apply(Project project) {
        ensureAndroidPluginAppliedTo(project)
        MonkeyRunnerExtension extension = project.extensions.create(MonkeyRunnerExtension.NAME, MonkeyRunnerExtension)
        extension.setDefaultsForOptionalProperties()

        project.afterEvaluate {
            extension.ensureMandatoryPropertiesPresent()
            configureTask(
                    project,
                    extension
            )
        }
    }

    private static void ensureAndroidPluginAppliedTo(Project project) {
        boolean missingAndroidPlugin = !project.plugins.hasPlugin('com.android.application')
        if (missingAndroidPlugin) {
            throw new GradleException('monkey runner plugin can only be applied after the Android plugin')
        }
    }

    private void configureTask(Project project, MonkeyRunnerExtension extension) {
        def runMonkeyAllTask = project.task(TASK_NAME)

        def android = project.extensions.findByName("android")
        android.command.devices().eachWithIndex { device, index ->

            def showOverlayTask = project.task("showOverlayDevice${index}", type: NotificationBarOverlay) {
                show = true
                deviceId = device.id
            }

            def monkeyTask = project.task("runMonkeyDevice${index}", type: TargetedMonkey) {
                packageName = extension.packageNameFilter
                events = extension.eventsCount
                deviceId = device.id
                logFileName = extension.logFileName
                categories = extension.categories
            }

            def hideOverlay = project.task("hideOverlayDevice${index}", type: NotificationBarOverlay) {
                show = false
                deviceId = device.id
            }

            def uninstallApp = project.task("uninstallMonkeyDevice${index}", type: TargetedUninstall) {
                packageName = extension.packageNameFilter
                deviceId = device.id
            }

            hideOverlay.dependsOn uninstallApp
            monkeyTask.dependsOn showOverlayTask
            monkeyTask.dependsOn extension.taskDependency
            monkeyTask.finalizedBy hideOverlay
            runMonkeyAllTask.dependsOn monkeyTask
        }
    }
}
