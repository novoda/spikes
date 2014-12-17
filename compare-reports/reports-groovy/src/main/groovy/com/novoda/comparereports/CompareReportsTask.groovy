package com.novoda.comparereports

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.novoda.comparereports.bean.Checkstyle
import com.novoda.comparereports.bean.Report
import org.gradle.api.DefaultTask
import org.gradle.api.file.RelativePath
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecResult

public class CompareReportsTask extends DefaultTask {

    private static final String DESTINATION_PATH = "build/reports"

    final ObjectMapper mapper

    ReportsExtension extension

    CompareReportsTask() {
        mapper = new XmlMapper()
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @TaskAction
    def shipIt() {
        def compareReportsDir = "$DESTINATION_PATH/$project.name"

        // TODO handle when I already have the repo cloned (i.e. if you don't clean)
        // TODO handle when the desired branch isn't the main branch
        cloneRepo(compareReportsDir)
        generateMainBranchReports(compareReportsDir)

        def mainBranchFiles = project.fileTree(project.projectDir).exclude(compareReportsDir).include(extension?.checkstyleFiles)
        def currentBranchFiles = project.fileTree(compareReportsDir).include(extension?.checkstyleFiles)

        currentBranchFiles.each { File currentBranchFile ->
            String fileName = RelativePath.parse(true, currentBranchFile.absolutePath).lastName
            File mainBranchFile = mainBranchFiles.find { File mainBranchFile -> mainBranchFile.name.equals(fileName) }
            compareReports(mainBranchFile, currentBranchFile)
        }
    }

    private ExecResult cloneRepo(compareReportsDir) {
        String remoteUri = getGitRemoteUri()
        project.exec {
            commandLine "git"
            args 'clone', '-q', remoteUri, compareReportsDir
        }
    }

    private String getGitRemoteUri() {
        def stdout = new ByteArrayOutputStream()
        project.exec {
            commandLine "git"
            args 'ls-remote', '--get-url'
            standardOutput = stdout
        }
        stdout.toString().readLines()[0]
    }

    private ExecResult generateMainBranchReports(compareReportsDir) {
        project.exec {
            workingDir compareReportsDir
            commandLine "./gradlew"
            args 'check', '-q', '-x', 'test'
        }
    }

    private void compareReports(File mainBranchFile, File currentBranchFile) {
        def oldCheckstyle = mapper.readValue(mainBranchFile, Checkstyle.class)
        def newCheckstyle = mapper.readValue(currentBranchFile, Checkstyle.class)

        // TODO: Handle different file paths in the checkstyle report

        Report report = Reporter.generate(oldCheckstyle, newCheckstyle)
        println()
        println()
        println report.fixedIssues.forHumans()
        println()
        println()
        println report.introducedIssues.forHumans()
        println()
        println()
    }


}

