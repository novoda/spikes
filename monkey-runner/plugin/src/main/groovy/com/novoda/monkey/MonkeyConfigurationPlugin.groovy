package com.novoda.monkey

import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project

public class MonkeyConfigurationPlugin implements Plugin<Project> {

    private static final String EXTENSION_NAME = 'monkeyRunner'
    private static final String TASK_NAME = 'runMonkeyAll'

    @Override
    public void apply(Project project) {
        ensureAndroidPluginAppliedTo(project)
        MonkeyRunnerExtension extension = project.extensions.create(EXTENSION_NAME, MonkeyRunnerExtension)
        extension.setDefaultsForOptionalProperties()

        project.afterEvaluate {
            extension.ensureMandatoryPropertiesPresent()
            configureTask(project, extension.taskDependency, extension.eventsCount)
        }
    }

    private static void ensureAndroidPluginAppliedTo(Project project) {
        boolean missingAndroidPlugin = !project.plugins.hasPlugin('com.android.application')
        if (missingAndroidPlugin) {
            throw new GradleException('monkey runner plugin can only be applied after the Android plugin')
        }
    }

    private void configureTask(Project project, String taskDependency, Integer eventsCount) {
        def runMonkeyAllTask = project.task(TASK_NAME)

        def android = project.extensions.findByName("android")
        android.command.devices().eachWithIndex { device, index ->

            def showOverlayTask = project.task("showOverlayDevice${index}", type: NotificationBarOverlay) {
                show = true
                deviceId = device.id
            }

            def monkeyTask = project.task("runMonkeyDevice${index}", type: TargetedMonkey, dependsOn: taskDependency) {
                packageName = "com.novoda.monkey"
                events = eventsCount
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

    static class MonkeyRunnerExtension {

        String taskDependency
        Integer eventsCount

        void setDefaultsForOptionalProperties() {
            eventsCount = 50000
        }

        void ensureMandatoryPropertiesPresent() {
            if (taskDependency == null) {
                notifyMissingProperty('taskDependency')
            }
        }

        static void notifyMissingProperty(String propertyName) {
            throw new IllegalArgumentException(propertyName + ' is not specified')
        }
    }
}
