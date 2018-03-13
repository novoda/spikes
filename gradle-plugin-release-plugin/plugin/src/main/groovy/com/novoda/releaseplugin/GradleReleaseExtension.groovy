package com.novoda.releaseplugin

import org.gradle.api.Project
import org.gradle.api.provider.Property

class GradleReleaseExtension {

    private final Project project

    final Property<String> changelog
    final Property<String> version

    GradleReleaseExtension(Project project) {
        this.project = project
        changelog = project.objects.property(String)
        version = project.objects.property(String)
    }

    void setChangelog(String changelog) {
        this.changelog.set(changelog)
    }

    void setVersion(String version) {
        this.version.set(version)
    }
}

