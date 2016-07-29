module.exports = {
  rule: mostRecentGif
}

var helper = require('./message-helper.js');

function mostRecentGif(messages) {
  var gifMessages = messages.filter(each => {
    return each.text.indexOf('.gif') !== -1;
  });

  if (gifMessages.length === 0) {
    return null;
  }

  gifMessages.sort(helper.sortByTimestamp);

  var latestMessage = gifMessages[0];
  latestMessage.gif = findGifUrlFrom(latestMessage.text.replace('<', '').replace('>', ''));

  return latestMessage;
}

function findGifUrlFrom(text) {
  var result = text.match(/(https?:[^\s]+)/g);
  return result && result[0];
}
