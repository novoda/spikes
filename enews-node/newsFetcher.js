module.exports = NewsFetcher;

var Slack = require('./slack.js');

function NewsFetcher(token) {
  const GENERAL_CHANNEL_ID = 'C029J9QTH'

  var slack = new Slack(token);

  this.getEnews = function(oldest, latest, callback) {
    var wrap = function(messages) {
        callback(messages.filter(isEnewsMessage));
    };
    slack.getMessages(GENERAL_CHANNEL_ID, oldest, latest, wrap);
  }

  function isEnewsMessage(message) {
    return isEnews(message.text)
  }

  function isEnews(messageText) {
    return contains(messageText, '#enews') || contains(messageText, '#eNews') || contains(messageText, '#C0YNBKANM');
  }

  function contains(input, check) {
    return input.indexOf(check) > -1;
  }

}
