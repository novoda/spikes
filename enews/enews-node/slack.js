const http = require('request-promise-native');

const HISTORY_ENDPOINT = 'https://slack.com/api/channels.history'
const USER_ENDPOINT = 'https://slack.com/api/users.info'
const MESSAGE_COUNT = 1000;

module.exports = Slack;

function Slack(token) {
  this.getMessages = function getMessages(channel, oldest, latest, callback) {
    getAllHistory(channel, oldest, latest)
      .then(callback);
  }

  function getAllHistory(channel, oldest, latest, messages) {
    var historyRequest = createHistoryRequest(channel, oldest, latest);
    return getSlackHistory(historyRequest).then(result => {
      if (result.hasMore) {
        return getAllHistory(
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
