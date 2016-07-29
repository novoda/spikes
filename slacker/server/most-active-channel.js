module.exports = {
  rule: mostActiveChannel
}

var helper = require('./message-helper.js');

function mostActiveChannel(messages) {
  var timeSortedMessages = messages.sort(helper.sortByTimestamp);
  var allMessages = helper.flattenToChannel(timeSortedMessages);
  allMessages.sort(helper.sortByCount);
  return allMessages[0];
}
