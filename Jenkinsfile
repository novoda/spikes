node ('android') {
    stage 'Checkout'
    echo "My branch is: ${env.BRANCH_NAME}"
    
    echo "start new commands"
    sh "ifconfig"
    sh "touch /home/jenkins/testAjeje"
    sh "ls /home/jenkins"
    echo "end new commands"
    
    checkout scm

    dir ("MagicMirror") {
        stage "Build"
        sh "./gradlew clean assemble"

        stage "Test"
        sh "./gradlew test"
        step([$class     : 'JUnitResultArchiver',
              testResults: '**/TEST*.xml']
        )

        stage "Static Analysis"
        sh "./gradlew lint"
        step([$class             : 'LintPublisher',
              canComputeNew      : false,
              pattern            : '**/outputs/lint-results-*.xml',
              unstableTotalHigh  : '6',
              unstableTotalNormal: '29']
        )
    }
}
