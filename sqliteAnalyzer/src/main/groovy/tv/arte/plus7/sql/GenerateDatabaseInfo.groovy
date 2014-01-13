package tv.arte.plus7.sql

import com.novoda.sqlite.Analyzer
import com.novoda.sqlite.ColumnsPrinter
import com.novoda.sqlite.Generator
import com.novoda.sqlite.MigrationsInDir
import com.novoda.sqlite.Migrator
import com.novoda.sqlite.Printer
import com.novoda.sqlite.TablesPrinter
import com.novoda.sqlite.model.Database
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

/**
 * This task runs the SqliteAnalyzer to generate code to describe the database tables and columns.
 *
 * We use input and output directory annotations to ensure the task is run on any changes in the migrations directory
 * or when the output is removed.
 */
class GenerateDatabaseInfo extends DefaultTask {

    @InputDirectory
    File migrationsDir

    @OutputDirectory
    File outputDir

    String packageName = "com.novoda.database"

    @TaskAction
    void generate() {
        Database database = analyzeDb()
        generateCode(database)
    }

    private Database analyzeDb() {
        def arteMigrations = new MigrationsInDir(migrationsDir)
        def connection = new Migrator(arteMigrations).runMigrations()
        def database = new Analyzer(connection).analyze()
        connection.close()
        database
    }

    private void generateCode(Database database) {
        Printer[] printers = [new ColumnsPrinter(database), new TablesPrinter(database)]
        def codeGenerator = new Generator(makeFileDir(), packageName, printers)
        codeGenerator.print()
    }

    private File makeFileDir() {
        String packageAsDir = packageName.replaceAll(~/\./, "/")
        def fileDir = new File(outputDir, packageAsDir)
        fileDir.mkdirs()
        return fileDir
    }
}

