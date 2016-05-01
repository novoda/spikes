var fs = require('fs');
var exec = require('child_process').exec;

module.exports = {
  check: function(directory, callback) {
    return check(directory, callback);
  }
};

function check(directory, callback) {
  checkInt(directory, directory, callback);
}

function checkInt(gitRepoPath, currentDirectory, callback) {
  fs.readdir(currentDirectory, function(err, items) {
    items.forEach(function(it) {
      var file = currentDirectory + '/' + it;
      checkFile(gitRepoPath, file, callback);
    })
  });
}

function checkFile(gitRepoPath, file, callback) {
  fs.stat(file, function(err, stats) {
      if (!stats.isDirectory()) {
        return;
      }
      executeShortLog(gitRepoPath, file, function(result) {
        callback(result);
        checkInt(gitRepoPath, file, callback);
      });
  });
}

function executeShortLog(gitRepoPath, file, callback) {
  exec(shortlogCommand(gitRepoPath, file), { cwd : "/tmp/" }, function(err, stdout, stderr) {
    if (stdout.length == 0) {
      return;
    }

    var result = parseShortLog(file, stdout);
    callback(result);
  });
}

function shortlogCommand(gitRepoPath, file) {
  return 'git -C ' + gitRepoPath + ' shortlog -n -s -- ' + file + ' < /dev/tty';
}

function parseShortLog(file, shortlog) {
  var lines = shortlog.replace(/[\t\r]/g," ").split("\n").filter(filterEmpty);
  var contributors = marshallToContributors(lines);
  return {
    directory: file,
    contributors: contributors
  }
}

function filterEmpty(it) {
  return it && it.length > 0;
}

function marshallToContributors(lines) {
  var contributors = [];
  lines.forEach(function(each) {
    var split = each.trim().split(/(\d+)/g).filter(filterEmpty);
    var contributor = {
      author: split[1].trim(),
      commitCount: split[0].trim()
    };
    contributors.push(contributor)
  });
  return contributors;
}
