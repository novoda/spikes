module.exports = {
  rule: mostActiveChannel
}

var helper = require('./message-helper.js');

function mostActiveChannel(dataStore, messages) {
  var timeSortedMessages = messages.sort(helper.sortByTimestamp);
  var allMessages = helper.flattenToChannel(timeSortedMessages);
  allMessages.sort(helper.sortByCount);
  var mostActiveChannel = allMessages.length > 0 ? allMessages[0] : null;
  return {
    widgetKey: 'mostActiveChannel',
    payload: createPayload(dataStore, mostActiveChannel)
  };
}

function createPayload(dataStore, message) {
  if (message) {
    return {
      channel: dataStore.getChannelById(message.key),
      message: message
    };
  } else {
    return null;
  }
}
