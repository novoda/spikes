module.exports = Slack;

function Slack(token) {
  const HISTORY_ENDPOINT = 'https://slack.com/api/channels.history'
  const MESSAGE_COUNT = 1000;

  var httpClient = require('request');

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
    httpClient.get(request, function(error, response, body) {
      var jsonBody = JSON.parse(body);
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
  
}
