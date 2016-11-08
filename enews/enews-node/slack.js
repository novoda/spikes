const http = require('request-promise-native');

const HISTORY_ENDPOINT = 'https://slack.com/api/channels.history'
const USER_ENDPOINT = 'https://slack.com/api/users.info'
const MESSAGE_COUNT = 1000;

function Slack(token) {
  this.token = token;
}

Slack.prototype.getMessages = function(channel, oldest, latest) {
  return getAllHistory(this, channel, oldest, latest);
}

function getAllHistory(self, channel, oldest, latest, messages) {
  var historyRequest = createHistoryRequest(self.token, channel, oldest, latest);
  return getSlackHistory(historyRequest).then(result => {
    if (result.hasMore) {
      return getAllHistory(
          self,
          channel,
          oldest,
          result.reducedLatest,
          messages.concat(result.messages)
        );
    } else {
      return Promise.resolve(result.messages);
    }
  });
}

function getSlackHistory(request) {
  return http(request).then(response => {
    var jsonBody = JSON.parse(response);
    var messages = jsonBody.messages.filter(isValidMessage);
    var reducedLatest = messages[messages.length - 1].ts;
    return Promise.resolve({
      messages: messages,
      hasMore: jsonBody.has_more,
      nextLatest: reducedLatest
    })
  });
}

const isValidMessage = (message) => !message.bot_id && message.type == 'message';

function createHistoryRequest(token, channel, oldest, latest) {
  return {
    url: HISTORY_ENDPOINT,
    qs: {
      token: token,
      channel: channel,
      count: MESSAGE_COUNT,
      oldest: oldest,
      latest: latest
    }
  }
};

Slack.prototype.getUser = function(userId) {
  var request = createUserRequest(this.token, userId);
  return http(request).then(response => {
    return Promise.resolve(JSON.parse(response).user);
  });
}

function createUserRequest(token, userId) {
  return {
    url: USER_ENDPOINT,
    qs: {
      token: token,
      user: userId
    }
  }
};

module.exports = Slack;
