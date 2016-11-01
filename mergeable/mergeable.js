const GitHub = require('github-api');
const Slack = require('@slack/client').WebClient;
const secrets = require('./secrets');

const OPEN_PRS = { state: 'open' }

function Mergeable(secrets) {
  this.gitHubSecrets = secrets.gitHub;
  this.slackRecipient = secrets.slack.recipient;
  this.gitHub = new GitHub(secrets.gitHub.credentials);
  this.slack = new Slack(secrets.slack.token);
}

Mergeable.prototype.checkMergeability = function() {
  const repo = this.gitHub.getRepo(
    this.gitHubSecrets.repoOwner,
    this.gitHubSecrets.repoName
  );
  return repo.listPullRequests(OPEN_PRS)
    .then(getIndividualPullRequests(repo))
    .then(findUnmergeablePrs)
    .then(notifySlack(this.slack, this.slackRecipient));
}

function getIndividualPullRequests(repo) {
  return function(listOfPrs) {
    const allPrs = listOfPrs.data.map(each => {
      return repo.getPullRequest(each.number)
        .then(toData);
    });
    return Promise.all(allPrs);
  }
}

function toData(pullRequest) {
  return Promise.resolve(pullRequest.data);
}

function findUnmergeablePrs(pullRequests) {
  const unmergablePrs = pullRequests.filter(filterUnmergeable)
  console.log(unmergablePrs);
  return Promise.resolve(unmergablePrs);
}

const filterUnmergeable = (pr) => !pr.mergeable;

function notifySlack(slack, slackRecipient) {
  return function(unmergeablePrs) {
    return Promise.all(unmergeablePrs.map(each => {
      return slack.chat.postMessage(slackRecipient, createSlackMessage(each), { as_user: true})
        .then(Promise.resolve(unmergeablePrs));
    }));
  }
}

function createSlackMessage(pr) {
  return `<${pr.html_url}|${pr.title}> has conflicts with master!`;
}

module.exports = Mergeable;
