module.exports = {
  rule: biggestSlacker
}

var helper = require('./message-helper.js');

function biggestSlacker(dataStore, messages) {
  var timeSortedMessages = messages.sort(helper.sortByTimestamp);
  var allMessages = helper.flattenToUser(timeSortedMessages);
  allMessages.sort(helper.sortByCount);
  var biggestSlacker = allMessages.length > 0 ? allMessages[0] : null;
  return {
    thingKey: 'biggestSlacker',
    payload: createPayload(dataStore, biggestSlacker)
  };
}

function createPayload(dataStore, message) {
  if (message) {
    return {
      user: dataStore.getUserById(message.key),
      message: message
    };
  } else {
    return null;
  }
}
