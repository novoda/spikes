module.exports = {
  rule: biggestSlacker
}

var helper = require('./message-helper.js');

function biggestSlacker(messages) {
  var timeSortedMessages = messages.sort(helper.sortMessagesByTimestamp);
  var allMessages = helper.flattenToUser(timeSortedMessages);
  allMessages.sort(helper.sortByMessagesLength);
  return allMessages[0];
}
