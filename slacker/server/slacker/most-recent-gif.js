module.exports = {
  rule: mostRecentGif
}

var helper = require('./message-helper.js');

function mostRecentGif(dataStore, messages) {
  var gifMessages = messages.filter(each => {
    return each.text.indexOf('.gif') !== -1;
  });
  gifMessages.sort(helper.sortByTimestamp);

  var gifMessage = gifMessages.length > 0 ? gifMessages[0] : null;
  return {
    thingKey: 'mostRecentGif',
    payload: createPayload(dataStore, gifMessage)
  };
}

function createPayload(dataStore, gifMessage) {
  if (gifMessage) {
    gifMessage.gif = findGifUrlFrom(gifMessage.text.replace('<', '').replace('>', ''));
    return {
      user: dataStore.getUserById(gifMessage.user),
      mostRecentGif: gifMessage
    };
  } else {
    return null;
  }
}

function findGifUrlFrom(text) {
  var result = text.match(/(https?:[^\s]+)/g);
  return result && result[0];
}
