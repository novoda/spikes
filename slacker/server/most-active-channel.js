module.exports = {
  rule: mostActiveChannel
}

var helper = require('./message-helper.js');

function mostActiveChannel(messages) {
  var timeSortedMessages = messages.sort(helper.sortMessagesByTimestamp);
  var allMessages = helper.flattenToChannel(timeSortedMessages);
  allMessages.sort(helper.sortByMessagesLength);
  return allMessages[0];
}
