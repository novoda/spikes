package com.novoda.monkey

class MonkeyRunnerExtension {

    public static final String NAME = 'monkeyRunner'

    String taskDependency
    Integer eventsCount
    String packageNameFilter
    String logFileName
    List<String> categories
    boolean useMonkeyTrap

    void setDefaultsForOptionalProperties() {
        eventsCount = 50000
        logFileName = 'monkey.log'
        useMonkeyTrap = true
    }

    void ensureMandatoryPropertiesPresent() {
        if (taskDependency == null) {
            notifyMissingProperty('taskDependency')
        }
        if (packageNameFilter == null) {
            notifyMissingProperty('packageNameFilter')
        }
    }

    private static void notifyMissingProperty(String propertyName) {
        throw new IllegalArgumentException("${NAME}.${propertyName} is not specified")
    }
}
