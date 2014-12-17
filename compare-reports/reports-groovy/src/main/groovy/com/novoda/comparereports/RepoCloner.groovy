package com.novoda.comparereports

class RepoCloner {

    private static final String SCRIPT = """
        #!/bin/sh

        original_working_dir=`pwd`
        destination_dir='build/reports/comparison'

        mkdir -p \$destination_dir
        cd \$destination_dir

        remote_url=`git ls-remote --get-url`
        git clone \$remote_url .

        ./gradlew check -q

        destination_full_path=`pwd`
        cd \$original_working_dir

        echo "\${destination_full_path}"
    """

    String pow() {
        def sout = new StringBuffer(), serr = new StringBuffer()
        def proc = "./../spikes/compare-reports/bash-scripts/fetch-main-branch.sh".execute() // TODO why does this not work?????????????
        proc.consumeProcessOutput(sout, serr)
        proc.waitFor()
        sout.readLines().last()
    }

}
