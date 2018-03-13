package com.novoda.releaseplugin

import org.gradle.api.Project
import org.gradle.api.provider.PropertyState

class GradleReleaseExtension {

    private final Project project

    final PropertyState<String> changelog
    final PropertyState<String> version

    GradleReleaseExtension(Project project) {
        this.project = project
        changelog = project.property(String)
        version = project.property(String)
    }

    void setChangelog(String changelog) {
        this.changelog.set(changelog)
    }

    void setVersion(String version) {
        this.version.set(version)
    }
}

