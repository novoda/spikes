package com.novoda.buildproperties

import org.apache.commons.configuration2.Configuration
import org.apache.commons.configuration2.FileBasedConfiguration
import org.apache.commons.configuration2.PropertiesConfiguration
import org.apache.commons.configuration2.builder.FileBasedBuilderProperties
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder
import org.apache.commons.configuration2.builder.fluent.Parameters
import org.gradle.api.Project

class BuildProperties {

    final Project project;
    final URI uri;
    final Configuration configuration;

    BuildProperties(Project project, String path) {
        this.project = project
        this.uri = URI.create(new File(path as String).getAbsolutePath())
        FileBasedBuilderProperties params = new Parameters().fileBased().setFileName(path)
        FileBasedConfigurationBuilder<FileBasedConfiguration> builder =
                new FileBasedConfigurationBuilder<FileBasedConfiguration>(PropertiesConfiguration.class).configure(params)
        this.configuration = builder.getConfiguration();
    }

    int getInt(String property) {
        checkPropertyOrThrow(property)
        Integer.parseInt(getPropertyValue(property))
    }

    double getDouble(String property) {
        checkPropertyOrThrow(property)
        Double.parseDouble(getPropertyValue(property))
    }

    File getFile(String property) {
        String filePath = getString(property)
        if (URI.create(filePath).getScheme() != null) {
            new File(filePath)
        } else {
            new File(uri.resolve(filePath).toString())
        }
    }

    String getString(String property) {
        checkPropertyOrThrow(property)
        getPropertyValue(property)
    }

    private void checkPropertyOrThrow(String property) {
        if (getPropertyValue(property) == null) {
            throw new RuntimeException("Please define correct value for \"${property}\"")
        }
    }

    boolean contains(String name) {
        return getPropertyValue(name)
    }

    private String getPropertyValue(String property) {
        if (this.project.hasProperty(property)) {
            return this.project.getProperties().get(property)
        }

        return configuration.getString(property);
    }

}
