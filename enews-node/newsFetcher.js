module.exports = NewsFetcher;

function NewsFetcher(token) {
  const GENERAL_CHANNEL_ID = 'C029J9QTH'
  const HISTORY_ENDPOINT = 'https://slack.com/api/channels.history'
  const MESSAGE_COUNT = 1000;

  var httpClient = require('request');

  this.getEnews = function getEnews(oldest, latest, callback) {
    var allEnews = [];
    var handler = function(enews, hasMore, reducedLatest) {
      allEnews = allEnews.concat(enews);
      if (hasMore) {
        getSlackHistory(createHistoryRequest(oldest, reducedLatest), handler);
      } else {
        callback(allEnews);
      }
    }
    getSlackHistory(createHistoryRequest(oldest, latest), handler);
  }

  function getSlackHistory(request, callback) {
    httpClient.get(request, function(error, response, body) {
      var jsonBody = JSON.parse(body);
      var messages = jsonBody.messages;
      var enews = parseMessages(messages);
      var reducedLatest = messages[messages.length - 1].ts;
      callback(enews, jsonBody.has_more, reducedLatest);
    });
  }

  function parseMessages(messages) {
    var enews = [];
    messages.forEach(function(message) {
      if (isEnewsMessage(message)) {
        enews.push(message.text);
      }
    });
    return enews;
  }

  function isEnewsMessage(message) {
    return isValidMessage(message) && isEnews(message.text)
  }

  function isEnews(messageText) {
    return contains(messageText, '#enews') || contains(messageText, '#eNews') || contains(messageText, '#C0YNBKANM');
  }

  function isValidMessage(message) {
    return !message.bot_id && message.type == 'message'
  }

  function contains(input, check) {
    return input.indexOf(check) > -1;
  }

  function createHistoryRequest(oldest, latest) {
    return {
      url: HISTORY_ENDPOINT,
      qs: {
        token: token,
        channel: GENERAL_CHANNEL_ID,
        count: MESSAGE_COUNT,
        oldest: oldest,
        latest: latest
      }
    }
  };

}
