const GitHubPair = require('./github-pair');
const Spreadsheet = require('./google-pair-sheet');

function Pairer(config) {
  this.gitHubPair = new GitHubPair(config.gitHub);
  this.spreadsheet = new Spreadsheet(config.google);
}

Pairer.prototype.updatePairs = function(repoName, prNumber) {
  return this.gitHubPair.getPairs(repoName, prNumber)
  .then(toPair)
  .then(this.spreadsheet.update.bind(this.spreadsheet));
}

function toPair(gitHubPair) {
  return {
    first: gitHubPair.author,
    second: gitHubPair.pairedWith
  };
}

module.exports = Pairer;
