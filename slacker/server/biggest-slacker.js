module.exports = {
  rule: biggestSlacker
}

var helper = require('./message-helper.js');

function biggestSlacker(messages) {
  var timeSortedMessages = messages.sort(helper.sortByTimestamp);
  var allMessages = helper.flattenToUser(timeSortedMessages);
  allMessages.sort(helper.sortByCount);
  return allMessages[0];
}
