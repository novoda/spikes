const fs = require('fs');
const exec = require('child_process').exec;

module.exports = {
  check: function(directory, callback) {
    return check(directory, callback);
  }
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
  let contributors = marshallToContributors(lines);
  return {
    directory: file,
    contributors: contributors
  }
}

let filterEmpty = (it => it && it.length > 0)

function marshallToContributors(lines) {
  let contributors = [];
  lines.forEach(each => {
    let segments = toCleanSegments(each);
    let contributor = {
      author: segments[1].trim(),
      commitCount: segments[0].trim()
    };
    contributors.push(contributor)
  });
  return contributors;
}

let toCleanSegments = (it => it.trim().split(/(\d+)/g).filter(filterEmpty))
