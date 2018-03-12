package com.novoda.releaseplugin.internal

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

class ParseChangeLogTask extends DefaultTask {

    @InputFile
    File changelog

    @OutputFile
    File releaseChangelog

    String version

    ParseChangeLogTask() {
        group = 'changelog'
        description = 'Extracts the changelog for the current release'
    }

    @TaskAction
    void run() {
        releaseChangelog.text = extractChangelog()
    }

    String extractChangelog() {
        String fullChangelog = changelog.text
        def latestChangelog = (fullChangelog =~ /\[Version ${version}.*\n-*([\s\S]*?)\[Version.*\n-*/)
        if (latestChangelog.size() > 0) {
            return latestChangelog[0][1].trim()
        }

        def firstChangelog = (fullChangelog =~ /\[Version ${version}.*\n-*([\s\S]*)/)
        if (firstChangelog.size() > 0) {
            return firstChangelog[0][1].trim()
        }
        throw new GradleException("No changelog found for version $version")
    }
}
