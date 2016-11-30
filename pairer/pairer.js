const GitHubPair = require('./github-pair');

function Pairer(config) {
  this.gitHubPair = new GitHubPair(config.gitHub);
}

Pairer.prototype.updatePairs = function(repoName, prNumber) {
  return this.gitHubPair.getPairs(repoName, prNumber);
}

module.exports = Pairer;
