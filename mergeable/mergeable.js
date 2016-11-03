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
  const unmergablePrs = pullRequests.filter(filterUnmergeable);
  return Promise.resolve({
    isCalculating: unmergablePrs.filter(filterCalculating).length > 0,
    prs: unmergablePrs
  });
}

const filterUnmergeable = (pr) => !pr.mergeable;
const filterCalculating = (pr) => pr.mergeable === null;

function notifySlack(slack, slackRecipient) {
  return function(result) {
    if (result.isCalculating) {
      return postToSlack(slack, slackRecipient, calculatingMessage())
        .then(Promise.resolve(result));
    } else if (result.prs.length === 0) {
      return Promise.resolve(result);
    } else {
      return Promise.all(result.prs.map(each => {
        const message = conflictMessage(each);
        return postToSlack(slack, slackRecipient, message)
          .then(Promise.resolve(result));
      }));
    }
  }
}

function calculatingMessage() {
  return `Github is still calculating conflicts ¯\\_(ツ)_/¯`;
}

function conflictMessage(pr) {
  return `<${pr.html_url}|${pr.title}> has conflicts with master!`;
}

function postToSlack(slack, slackRecipient, message) {
  return slack.chat.postMessage(slackRecipient, message, { as_user: true})
}

module.exports = Mergeable;
