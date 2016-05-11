package com.novoda.buildproperties

import org.gradle.api.Plugin
import org.gradle.api.Project

public class BuildPropertiesPlugin implements Plugin<Project> {
    @Override
    void apply(Project target) {
        BuildPropertiesConfig config = target.extensions.create('buildProperties', BuildPropertiesConfig.class)

        target.afterEvaluate {
            if (config.file == null) {
                return
            }
            BuildProperties buildProperties = new BuildProperties(target, config.file);
            target.extensions.getExtraProperties().set('buildProperties', buildProperties);
        }
    }
}
