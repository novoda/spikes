package com.gertherb.api.shared

class CredentialsPropertiesLoader {

    private final List<String> propertiesNeeded
    private final String propertiesPath

    CredentialsPropertiesLoader(String propertiesPath, List<String> propertiesNeeded) {
        this.propertiesNeeded = propertiesNeeded
        this.propertiesPath = propertiesPath
    }

    Properties load() {
        File file = getPropertiesFile()
        Properties properties = loadProperties(file)
        checkPropertiesAreValid(properties)
        properties
    }


    private static Properties loadProperties(File file) {
        Properties properties = new Properties()
        properties.load(new FileInputStream(file))
        properties
    }

    private File getPropertiesFile() {
        File file = new File(propertiesPath)
        if (!file.exists()) {
            throw new InvalidCredentialsException("You need to put a property file containing the credentials needed for the app in $propertiesPath")
        }
        file
    }

    private void checkPropertiesAreValid(Properties properties) {
        propertiesNeeded.each { property ->
            if (!properties.containsKey(property)) {
                throw new InvalidCredentialsException("'$property' property is not defined in $propertiesPath")
            }
            if (!properties.getProperty(property)) {
                throw new InvalidCredentialsException("No value is defined for '$property' in $propertiesPath")
            }
        }
    }

    void save(String property, String value) {
        File file = getPropertiesFile()
        Properties properties = loadProperties(file)
        properties.put(property, value)
        properties.store(new FileWriter(file), "")
    }

}
