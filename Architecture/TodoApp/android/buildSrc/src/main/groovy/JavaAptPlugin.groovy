import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.plugins.ide.idea.IdeaPlugin
import org.gradle.tooling.BuildException

class JavaAptPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        def aptConfiguration = project.configurations.create 'javapt'

        project.afterEvaluate {
            if (isJavaProject(project)) {
                applyToJavaProject(project, aptConfiguration)
            } else {
                throw new BuildException('The project isn\'t a java project', null)
            }
        }
    }

    private boolean isJavaProject(Project project) {
        project.plugins.hasPlugin('java')
    }

    void applyToJavaProject(Project project, def aptConfiguration) {
        File aptOutputDir = project.file('gen/javapt')
        project.sourceSets.main.compileClasspath += aptConfiguration
        project.sourceSets.main.java.srcDirs += aptOutputDir

        project.plugins.withType(IdeaPlugin) {
            project.idea.module {
                scopes.PROVIDED.plus += [aptConfiguration]
                generatedSourceDirs += aptOutputDir
            }
        }

        def addAptCompilerArgs = project.task('addAptCompilerArgs') << {

            project.compileJava.options.compilerArgs.addAll '-processorpath',
                    aptConfiguration.asPath, '-s', aptOutputDir.path

            project.compileJava.source = project.compileJava.source.filter {
                !it.path.startsWith(aptOutputDir.path)
            }

        }

        def cleanTask = project.task('cleanAptOutput') << {
            project.delete aptOutputDir
            project.delete project.buildDir
        }

        def createTask = project.task('createAptOutputFolder') << {
            aptOutputDir.mkdirs()
        }

        project.tasks.getByName('clean').dependsOn cleanTask

        createTask.dependsOn cleanTask
        addAptCompilerArgs.dependsOn createTask

        project.tasks.getByName('compileJava').dependsOn addAptCompilerArgs

    }

}
