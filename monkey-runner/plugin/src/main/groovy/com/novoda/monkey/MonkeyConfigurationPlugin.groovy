package com.novoda.monkey

import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project

class MonkeyConfigurationPlugin implements Plugin<Project> {

    private static final String TASK_NAME = 'runMonkeyAll'

    @Override
    void apply(Project project) {
        ensureAndroidPluginAppliedTo(project)
        ensureCommandPluginAppliedTo(project)

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
        ensurePluginIsApplied('com.android.application', project)
    }

    private static void ensureCommandPluginAppliedTo(Project project) {
        ensurePluginIsApplied('android-command', project)
    }

    private static void ensurePluginIsApplied(String plugin, Project project) {
        boolean isMissingPlugin = !project.plugins.hasPlugin(plugin)
        if (isMissingPlugin) {
            throw new GradleException("monkey runner plugin can only be applied after the ${plugin} plugin.\n" +
                    "In your build.gradle: apply plugin: '${plugin}'")
        }
    }

    private void configureTask(Project project, MonkeyRunnerExtension extension) {
        def runMonkeyAllTask = project.task(TASK_NAME)

        def android = project.android
        android.command.devices().eachWithIndex { device, index ->

            def monkeyTask = project.task("runMonkeyDevice${index}", type: TargetedMonkey) {
                packageName = extension.packageNameFilter
                deviceId = device.id
                logFileName = extension.logFileName
                monkey = [events: extension.eventsCount, categories: extension.categories]
            }

            def uninstallApp = project.task("uninstallMonkeyDevice${index}", type: TargetedUninstall) {
                packageName = extension.packageNameFilter
                deviceId = device.id
            }

            if (extension.useMonkeyTrap) {
                def showOverlayTask = project.task("showOverlayDevice${index}", type: NotificationBarOverlay) {
                    show = true
                    deviceId = device.id
                }

                def hideOverlay = project.task("hideOverlayDevice${index}", type: NotificationBarOverlay) {
                    show = false
                    deviceId = device.id
                }

                hideOverlay.dependsOn uninstallApp
                monkeyTask.dependsOn showOverlayTask
                monkeyTask.finalizedBy hideOverlay
            } else {
                monkeyTask.finalizedBy uninstallApp
            }

            monkeyTask.dependsOn extension.taskDependency
            runMonkeyAllTask.dependsOn monkeyTask
        }
    }
}
