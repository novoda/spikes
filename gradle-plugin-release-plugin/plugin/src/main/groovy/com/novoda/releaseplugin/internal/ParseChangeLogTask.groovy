package com.novoda.releaseplugin.internal

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

class ParseChangeLogTask extends DefaultTask {

    @Input
    final Property<String> changelog

    @Input
    final Property<String> version

    @OutputFile
    File releaseChangelog

    ParseChangeLogTask() {
        group = 'changelog'
        description = 'Extracts the changelog for the current release'
        changelog = project.objects.property(String)
        version = project.objects.property(String)
    }

    @TaskAction
    void run() {
        def changelog = extractChangelog()
        releaseChangelog.text = changelog
    }

    /**
     *  The changelog needs needs to follow this format:
        [Version 0.1](https://github.com/novoda/gradle-plugin-release-plugin/releases/tag/v0.1)
        --------------------------

        - Initial release.
     */
    String extractChangelog() {
        String fullChangelog = new File(changelog.get()).text
        def latestChangelog = (fullChangelog =~ /\[Version ${version.get()}.*\n-*([\s\S]*?)\[Version.*\n-*/)
        if (latestChangelog.size() > 0) {
            return latestChangelog[0][1].trim()
        }

        def firstChangelog = (fullChangelog =~ /\[Version ${version.get()}.*\n-*([\s\S]*)/)
        if (firstChangelog.size() > 0) {
            return firstChangelog[0][1].trim()
        }
        throw new GradleException("No changelog found for version $version.get()")
    }
}
