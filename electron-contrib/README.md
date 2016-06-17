Electron desktop app (nodejs) to attempt to show the ownership of all the directories of a project by checking the number of commits made by each user for each package.

<img width="801" alt="screen shot 2016-05-01 at 13 26 19" src="https://cloud.githubusercontent.com/assets/1848238/14941780/5baac1c8-0fa0-11e6-91cb-66aa306e3b3a.png">


Usage - 

Must have `git` on the path and a writable `/tmp/` directory (which should be everyone except windows peeps).

clone the repo, cd into `electron-contrib` and run `npm install && npm start`

specify the full path to the git repository to analyse and submit