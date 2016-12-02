const GitHub = require('github-api');

function GithubPair(config) {
  this.config = config;
  this.gitHub = new GitHub(config.credentials);
}

GithubPair.prototype.getPairs = function(repoName, prNumber) {
  const repo = this.gitHub.getRepo(
    this.config.repoOwner,
    repoName
  );
  return repo.getPullRequest(prNumber)
    .then(result => result.data)
    .then(doWork)
}

function doWork(pr) {
  if (pr.body.includes('###### Paired with') &&
  pr.body.substring(pr.body.indexOf('###### Paired with')).includes('@')) {
    return Promise.resolve(asPair(pr));
  } else {
    return Promise.reject('no pair');
  }
}

function asPair(pr) {
  return {
    author: pr.user.login,
    pairedWith: findPair(pr.body)
  };
}

function findPair(body) {
  return body.match(/@[a-z]+/i)[0].replace('@', '');
}

module.exports = GithubPair;
