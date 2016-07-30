module.exports = {
  rule: biggestSlacker
}

var helper = require('./message-helper.js');

function biggestSlacker(dataStore, messages) {
  var timeSortedMessages = messages.sort(helper.sortByTimestamp);
  var allMessages = helper.flattenToUser(timeSortedMessages);
  allMessages.sort(helper.sortByCount);
  var biggestSlacker = allMessages[0];
  var user = dataStore.getUserById(biggestSlacker.key);
  return {
    thingKey: 'biggestSlacker',
    payload: {
      user: user,
      biggestSlacker: biggestSlacker
    }
  };
}
