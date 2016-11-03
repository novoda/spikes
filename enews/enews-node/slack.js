const http = require('request-promise-native');

module.exports = Slack;

function Slack(token) {
  const HISTORY_ENDPOINT = 'https://slack.com/api/channels.history'
  const USER_ENDPOINT = 'https://slack.com/api/users.info'
  const MESSAGE_COUNT = 1000;

  this.getMessages = function getMessages(channel, oldest, latest, callback) {
    var allMessages = [];
    var handler = function(messages, hasMore, reducedLatest) {
      allMessages = allMessages.concat(messages);
      if (hasMore) {
        var historyRequest = createHistoryRequest(channel, oldest, reducedLatest);
        getSlackHistory(historyRequest, handler);
      } else {
        callback(allMessages);
      }
    }
    var historyRequest = createHistoryRequest(channel, oldest, latest);
    getSlackHistory(historyRequest, handler);
  }

  function getSlackHistory(request, callback) {
    http(request).then(response => {
      var jsonBody = JSON.parse(response);
      var messages = jsonBody.messages.filter(isValidMessage);
      var reducedLatest = messages[messages.length - 1].ts;
      callback(messages, jsonBody.has_more, reducedLatest);
    });
  }

  function isValidMessage(message) {
    return !message.bot_id && message.type == 'message'
  }

  function createHistoryRequest(channel, oldest, latest) {
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

  this.getUser = function(userId, callback) {
    var request = createUserRequest(userId);
    http(request).then(response => {
      var jsonBody = JSON.parse(response);
      var user = jsonBody.user;
      callback(user);
    });
  }

  function createUserRequest(userId) {
    return {
      url: USER_ENDPOINT,
      qs: {
        token: token,
        user: userId
      }
    }
  };

}
