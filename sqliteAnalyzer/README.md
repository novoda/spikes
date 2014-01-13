# SQLiteAnalyzer

### Purpose
This spike aims to generate java utility code from given sql migrations.

### Mechanics
We generate an in-memory sqlite database, run the migrations on it and analyze the resulting tables to construct a DatabaseModel.
This model is then used to generate code.

### Libraries
We use [sqlite-jdbc](https://bitbucket.org/xerial/sqlite-jdbc) to create and analyze the database and 
[javawriter](https://github.com/square/javawriter) to generate java code. 

### Integration
To integrate sqliteAnalyzer into your project, let your master build depend on the sqliteAnalyzer project, either by
referencing it in the buildDependencies section or by checking it out under the `buildSrc` sub-directory and using the
following `build.gradle` in `buildSrc`:

```groovy
repositories {
    mavenCentral()
    maven {
        url "https://oss.sonatype.org/content/repositories/snapshots"
    }
}

rootProject.dependencies {
    compile project(':spikes:sqliteAnalyzer')
}
```

The code generation is then integrated with the gradle build via:

```groovy
/**
 * We register the DatabaseInfo generation task with all the variants as a java-generational task.
 * (Requires android gradle plugin >= 0.7.0)
 *
 * This makes the code available in the IDE.
 */
android.applicationVariants.all { variant ->
    // create a task that generates a java class
    File sourceFolder = file("${buildDir}/source/sqlite/${variant.dirName}")
    def javaGenerationTask = tasks.create(name: "generateSqlFor${variant.name.capitalize()}", type: tv.arte.plus7.sql.GenerateDatabaseInfo) {
        migrationsDir file("${projectDir.absolutePath}/src/main/assets/migrations")
        outputDir sourceFolder
        packageName "tv.arte.plus7.sql"
    }
    variant.registerJavaGeneratingTask(javaGenerationTask, sourceFolder)
}
```
