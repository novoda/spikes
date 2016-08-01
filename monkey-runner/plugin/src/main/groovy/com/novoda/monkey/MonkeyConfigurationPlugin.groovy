package com.novoda.monkey

import org.gradle.api.GradleException
import org.gradle.api.NamedDomainObjectFactory
import org.gradle.api.Plugin
import org.gradle.api.Project

public class MonkeyConfigurationPlugin implements Plugin<Project> {

    private static final String EXTENSION_NAME_MONKEY_RUNNER_CONFIG = 'monkeyRunner'
    public static final String PLUGIN_NAME_ANDROID = 'com.android.application'

    @Override
    public void apply(Project project) {
        ensureAndroidPluginAppliedTo(project)
        addMonkeyRunnerConfigExtensionTo(project)
        configureMonkeyRunner(project)
    }

    private static void ensureAndroidPluginAppliedTo(Project project) {
        boolean missingAndroidPlugin = !project.plugins.hasPlugin(PLUGIN_NAME_ANDROID)
        if (missingAndroidPlugin) {
            throw new GradleException('monkey runner plugin can only be applied after the Android plugin')
        }
    }

    private void configureMonkeyRunner(Project project) {
        { MonkeyRunnerConfig monkeyRunnerConfig ->
            project.afterEvaluate {
                reallyConfigureJob(project, monkeyRunnerConfig.getTaskDependency())
            }
        }
    }

    private void reallyConfigureJob(Project project, String taskDependency) {
        def runMonkeyAllTask = project.task('runMonkeyAll')

        def android = project.extensions.findByName("android")
        android.command.devices().eachWithIndex { device, index ->

            def showOverlayTask = project.task("showOverlayDevice${index}", type: NotificationBarOverlay) {
                show = true
                deviceId = device.id
            }

            def monkeyTask = project.task("runMonkeyDevice${index}", type: TargetedMonkey, dependsOn: taskDependency) {
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

    private void addMonkeyRunnerConfigExtensionTo(Project project) {
        def container = project.container(MonkeyRunnerConfig, new NamedDomainObjectFactory<MonkeyRunnerConfig>() {
            @Override
            MonkeyRunnerConfig create(String name) {
                return new MonkeyRunnerConfig()
            }
        })
        project.extensions.add(EXTENSION_NAME_MONKEY_RUNNER_CONFIG, container)
    }


    static class MonkeyRunnerConfig {
        private final String taskDependency

        MonkeyRunnerConfig(String taskDependency) {
            this.taskDependency = taskDependency
        }

        String getTaskDependency() {
            taskDependency
        }
    }
}
