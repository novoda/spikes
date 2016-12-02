const GitHub = require('github-api');
const PAIRED_WITH_TEMPLATE = '### Paired with'


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
    .then(findPairFromPr)
}

function findPairFromPr(pr) {
  if (pr.body.includes(PAIRED_WITH_TEMPLATE) &&
    pr.body.substring(pr.body.indexOf(PAIRED_WITH_TEMPLATE)).includes('@')) {
    return Promise.resolve(asPair(pr));
  } else {
    return Promise.reject('no pair');
  }
}

function asPair(pr) {
  return {
    author: pr.user.login,
    pairedWith: findPairFromBody(pr.body)
  };
}

function findPairFromBody(body) {
  return body.match(/@[a-z]+/i)[0].replace('@', '');
}

module.exports = GithubPair;
