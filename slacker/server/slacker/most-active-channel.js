module.exports = {
  rule: mostActiveChannel
}

var helper = require('./message-helper.js');

function mostActiveChannel(dataStore, messages) {
  var result = function(resolve, reject) {
    if (!messages || messages.length === 0) {
      reject('most active channel skipped, no messages');
    } else {
      resolve(createPayload(dataStore, messages));
    }
  }
  return new Promise(result);
}

function createPayload(dataStore, messages) {
  var timeSortedMessages = messages.sort(helper.sortByTimestamp);
  var allMessages = helper.flattenToChannel(timeSortedMessages);
  allMessages.sort(helper.sortByCount);
  var mostActiveChannel = allMessages[0];
  return {
    widgetKey: 'mostActiveChannel',
    payload: {
      channel: dataStore.getChannelById(mostActiveChannel.key),
      message: mostActiveChannel
    }
  };
}
