module.exports = {
  rule: mostActiveChannel
}

var helper = require('./message-helper.js');

function mostActiveChannel(dataStore, messages) {
  var timeSortedMessages = messages.sort(helper.sortByTimestamp);
  var allMessages = helper.flattenToChannel(timeSortedMessages);
  allMessages.sort(helper.sortByCount);
  var mostActiveChannel = allMessages[0];
  var channel = dataStore.getChannelById(mostActiveChannel.key);
  return {
    thingKey: 'mostActiveChannel',
    payload: {
      channel: channel,
      mostActiveChannel: mostActiveChannel
    }
  };
}
