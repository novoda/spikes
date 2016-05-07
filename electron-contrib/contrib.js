const fs = require('fs');
const exec = require('child_process').exec;

module.exports = {
  check: check
};

function check(directory, callback) {
  recursiveCheck(directory, directory, callback);
}

function recursiveCheck(gitRepoPath, currentDirectory, callback) {
  fs.readdir(currentDirectory, (err, items) => {
    items.forEach(it => {
      let file = currentDirectory + '/' + it;
      checkFile(gitRepoPath, file, callback);
    })
  });
}

function checkFile(gitRepoPath, file, callback) {
  fs.stat(file, (err, stats) => {
      if (!stats.isDirectory()) {
        return;
      }
      executeShortLog(gitRepoPath, file, result => {
        callback(result);
        recursiveCheck(gitRepoPath, file, callback);
      });
  });
}

function executeShortLog(gitRepoPath, file, callback) {
  exec(shortlogCommand(gitRepoPath, file), { cwd : '/tmp/' }, (err, stdout, stderr) => {
    if (stdout.length == 0) {
      return;
    }

    let result = parseShortLog(file, stdout);
    callback(result);
  });
}

function shortlogCommand(gitRepoPath, file) {
  return 'git -C ' + gitRepoPath + ' shortlog -n -s -- ' + file + ' < /dev/tty';
}

function parseShortLog(file, shortlog) {
  let lines = shortlog.replace(/[\t\r]/g, ' ').split('\n').filter(filterEmpty);
  let contributors = lines.map(marshallToContributors);
  return {
    directory: file,
    contributors: contributors
  };
}

let marshallToContributors = (each) => toContributor(toCleanSegments(each));

let toCleanSegments = (it) => it.trim().split(/(\d+)/g).filter(filterEmpty);

let toContributor = (it) => {
  return {
    author: it[1].trim(),
    commitCount: it[0].trim()
  };
}

let filterEmpty = (it) => it && it.length > 0;
