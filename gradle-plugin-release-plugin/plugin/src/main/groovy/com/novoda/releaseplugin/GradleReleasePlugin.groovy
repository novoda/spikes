package com.novoda.releaseplugin

import com.novoda.releaseplugin.internal.ParseChangeLogTask
import org.gradle.api.Plugin
import org.gradle.api.Project

class GradleReleasePlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        def pluginExtension = project.extensions.create('releasePlugin', GradleReleaseExtension, project)
        configureChangelog(project, pluginExtension)
    }

    private static void configureChangelog(Project project, GradleReleaseExtension pluginExtension) {
        def parseChangelog = project.tasks.create('parseReleaseChangelog', ParseChangeLogTask) { task ->
            task.changelog.set(pluginExtension.changelog)
            task.version.set(pluginExtension.version)
            task.releaseChangelog = project.file("${project.buildDir.path}/releaseChangelog.md")
        }

        project.tasks.create('printReleaseChangelog') { task ->
            task.group = 'changelog'
            task.description = 'Prints the changelog for the current release'

            task.dependsOn parseChangelog

            task.doLast {
                println(parseChangelog.releaseChangelog.text)
            }
        }
    }
}
