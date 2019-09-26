#!/bin/bash

# This script sets up an iterative loop to accelerate development of 
# server-side swift applications.  It periodically checks the filesystem
# contents to look for changes in any .swift files.  If any are found, 
# it will kill the currently running server process, rebuild, and restart 
# both the server process and lldb-server (debug) process.





# kills the executable for this project and the lldb debug server (if running)
function killServerProcesses() {
    pid="$(pgrep microservices-swift)"
    if [ "$pid" != "" ]
    then
        echo "killing microservices-swift"
        kill $pid
    fi

    pid="$(pgrep lldb-server)"
    if [ "$pid" != "" ]
    then
        echo "killing lldb-server"
        kill $pid
    fi
}

# called on signal interrupt (CTRL-C)
function interrupt ()
{
    printf "\n\nuser cancelled\n"
    killServerProcesses
    printf "\n"
    exit 2
}
 
# trap to handle signal interrupt 
trap "interrupt" INT

 



# Using an infinite loop/polling mechanism b/c it appears that 
# inotifywait (os file system events) does not pickup changes 
# on a volume, where the change orignates from the host OS (outside of the container)
# the poll just checks the contents of a recursive ls command for *files* only with full timestamps.
#
# If the ls output changes between loop iterations, the script 
# will kill the existing server process and restart it.
#
# This recursively looks for file changes for swift files only, 

echo "setting up watcher..."
lastOutput=""

# check if tools-utils.sh exists.  if so, assume you are inside of Dockerfile-tools container
if [ -e /swift-utils/tools-utils.sh ]
then
    cd /swift-project/
fi

while true; do
    unamestr=`uname`
    lsCommand='ls -lt -R --time-style=full-iso'
    if [[ "$unamestr" == 'Darwin' ]]; then
        lsCommand='ls -lT -R'
    fi
    temp="$($lsCommand | grep -i  ^-[-r][-w][-x][-r][-w][-x][-r][-w][-x].*\.swift$)"
    if [ "$lastOutput" != "$temp" ]
    then
        lastOutput="$temp"
        killServerProcesses

        echo "starting server..."
        if [ -e /swift-utils/tools-utils.sh ]
        then
            # assumes you are inside of Dockerfile-tools container
            # starts debug server on port 1024
            cd /swift-project/
            /swift-utils/tools-utils.sh debug microservices-swift 1024
        else
            # assumes you are running from "normal"/host operating system
            swift build
            if [[ $? == 0 ]]; then 
                # only start server is build was successful (result code 0)
                ./.build/debug/microservices-swift &
            fi
        fi
        
    fi
    sleep 2.0
done