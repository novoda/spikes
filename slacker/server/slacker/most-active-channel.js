module.exports = {
  rule: mostActiveChannel
}

var helper = require('./message-helper.js');

function mostActiveChannel(dataStore, messages) {
  var result = function(resolve, reject) {
    var timeSortedMessages = messages.sort(helper.sortByTimestamp);
    var allMessages = helper.flattenToChannel(timeSortedMessages);
    allMessages.sort(helper.sortByCount);
    var mostActiveChannel = allMessages.length > 0 ? allMessages[0] : null;
    resolve({
      widgetKey: 'mostActiveChannel',
      payload: createPayload(dataStore, mostActiveChannel)
    });
  }
  return new Promise(result);
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
