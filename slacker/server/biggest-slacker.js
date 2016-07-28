module.exports = {
  rule: biggestSlacker
}

function biggestSlacker(messages) {
  var timeSortedMessages = messages.sort(sortMessagesByTimestamp);

  var allMessages = flattenToUser(timeSortedMessages);
  allMessages.sort(sortByMessagesLength);;
  return allMessages[0];
}

var sortMessagesByTimestamp = (a, b) => {
  return b.ts - a.ts;
}

var sortByMessagesLength = (a, b) => {
  return b.messages.length - a.messages.length;
}

function flattenToUser(messages) {
  return flattenMessages(messages, 'user');
}

function flattenMessages(messages, flattenKey) {
  var dict = {};
  messages.forEach(each => {
    if(dict[each[flattenKey]]) {
      dict[each[flattenKey]].push(each);
    } else {
      dict[each[flattenKey]] = [each];
    }
  });
  return Object.keys(dict).map(key => {
      return {
        key: key,
        messages: dict[key]
      }
    });
}
