package com.novoda.comparereports

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.novoda.comparereports.bean.Checkstyle
import com.novoda.comparereports.bean.FixedIssues
import com.novoda.comparereports.bean.IntroducedIssues
import com.novoda.comparereports.bean.Report
import org.gradle.api.DefaultTask
import org.gradle.api.file.RelativePath
import org.gradle.api.tasks.TaskAction

public class CompareReportsTask extends DefaultTask {

    private static final String DESTINATION_PATH = "build/reports"

    final ObjectMapper mapper

    ReportsExtension extension

    CompareReportsTask() {
        mapper = new XmlMapper()
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @TaskAction
    def compareReports() {
        String compareReportsDir = "$DESTINATION_PATH/$project.name"

        // TODO handle when the desired branch isn't the main branch
        cleanOldMainReports(compareReportsDir)
        cloneRepo(compareReportsDir)
        generateMainBranchReports(compareReportsDir)

        def currentBranchFiles = project.fileTree(project.projectDir).exclude(compareReportsDir).include(extension?.checkstyleFiles)
        def mainBranchFiles = project.fileTree(compareReportsDir).include(extension?.checkstyleFiles)

        List<Report> reports = currentBranchFiles.collect { File currentBranchFile ->
            String fileName = RelativePath.parse(true, currentBranchFile.absolutePath).lastName
            File mainBranchFile = mainBranchFiles.find { File mainBranchFile -> mainBranchFile.name.equals(fileName) }
            generateReport(mainBranchFile, currentBranchFile, compareReportsDir)
        }
        printHumanReadable(reports)
    }

    def cleanOldMainReports(compareReportsDir) {
        // TODO This could be done in a better way. i.e. git pull instead of just dropping everything
        project.exec {
            commandLine "rm"
            args '-rf', compareReportsDir
        }
    }

    def cloneRepo(compareReportsDir) {
        String remoteUri = getGitRemoteUri()
        project.exec {
            commandLine "git"
            args 'clone', '-q', remoteUri, compareReportsDir
        }
    }

    String getGitRemoteUri() {
        def stdout = new ByteArrayOutputStream()
        project.exec {
            commandLine "git"
            args 'ls-remote', '--get-url'
            standardOutput = stdout
        }
        stdout.toString().readLines()[0]
    }

    def generateMainBranchReports(compareReportsDir) {
        project.exec {
            workingDir compareReportsDir
            commandLine "./gradlew"
            args 'check', '-q', '-x', 'test'
        }
    }


    Report generateReport(File mainBranchFile, File currentBranchFile, String compareReportsDir) {
        def mainCheckstyle = mapper.readValue(mainBranchFile, Checkstyle.class)
        def currentCheckstyle = mapper.readValue(currentBranchFile, Checkstyle.class)

        String currentCheckstyleBaseDir = project.projectDir.absolutePath
        String mainCheckstyleBaseDir = new File(project.projectDir, compareReportsDir).absolutePath

        Reporter.generate(mainCheckstyle, currentCheckstyle, mainCheckstyleBaseDir, currentCheckstyleBaseDir)
    }

    def printHumanReadable(List<Report> reports) {
        FixedIssues fixed = new FixedIssues(reports.collect { Report report -> report.fixedIssues }.flatten())
        IntroducedIssues introduced = new IntroducedIssues(reports.collect { Report report -> report.introducedIssues }.flatten())
        println()
        println fixed.forHumans()
        println introduced.forHumans()
        println()
    }


}

