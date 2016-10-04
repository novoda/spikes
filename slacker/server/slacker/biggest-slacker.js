module.exports = {
  rule: biggestSlacker
}

var helper = require('./message-helper.js');

function biggestSlacker(dataStore, messages) {
  var result = function(resolve, reject) {
    if (!messages || messages.length === 0) {
      reject('biggest slacker skipped, no messages');
    } else {
      resolve(createPayload(dataStore, messages));
    }
  }
  return new Promise(result);
}

function createPayload(dataStore, messages) {
  var timeSortedMessages = messages.sort(helper.sortByTimestamp);
  var allMessages = helper.flattenToUser(timeSortedMessages);
  allMessages.sort(helper.sortByCount);
  var biggestSlacker = allMessages[0];
  return {
    widgetKey: 'biggestSlacker',
    payload: {
      user: dataStore.getUserById(biggestSlacker.key),
      message: biggestSlacker
    }
  };
}
